package com.briup.client.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.briup.backup.IBackups;
import com.briup.bean.BIDR;
import com.briup.client.IGather;
import com.briup.common.IConfiguration;
import com.briup.common.IConfigurationAWare;

public class GatherImpl implements IGather, IConfigurationAWare {
	private String path; // 采集数据的路径
	private String gatherFileName; // 备份文件的名字
	private String countFileName; // 备份文件的名字
	private IBackups backups; // 备份模快
	// 获取日志
	private Logger logger = Logger.getLogger("clientLogger");

	/**
	 * 初始化数据
	 */
	@Override
	public void init(Properties properties) {
		path = properties.getProperty("path");
		gatherFileName = properties.getProperty("gatherFileName");
		countFileName = properties.getProperty("countFileName");
	}

	/**
	 * 获取备份模快数据
	 */
	@Override
	public void setConfiguration(IConfiguration configuration) {
		backups = configuration.getBackups();
	}

	/**
	 * 采集数据
	 */
	@Override
	public Collection<BIDR> gather() throws Exception {
		logger.info("开始采集数据");
		List<BIDR> bidrs = new ArrayList<>(); // 保存完整的记录
		Map<String, BIDR> map = new HashMap<String, BIDR>(); // 保存不完整记录
		Integer skip = 0; // 要跳过的数目
		File file = new File(path); // 构建解析原文件
		if (file.exists()) {
			BufferedReader reader = new BufferedReader(new FileReader(file)); // 解析原文件
			// 读取不完整数据的备份文件和要跳过的备份数据文件
			logger.info("读取备份数据");
			Object count = backups.load(countFileName, IBackups.LOAD_REMOVE); // 要跳过备份数据文件
			if (count != null) {
				skip += (Integer) count;
				logger.info("这一次采集的数据要跳过的字符:" + skip);
				reader.skip(skip);
			}
			Map<String, BIDR> loadMap = (Map<String, BIDR>) backups.load(gatherFileName, IBackups.LOAD_REMOVE);
			if (loadMap != null) {
				logger.info("上一次采集遗留的不完整数据大小为：" + loadMap.size());
				map.putAll(loadMap);
			}
			String line = null;
			while ((line = reader.readLine()) != null) {
				skip = skip + line.length() + 2; // 要跳过的数目
				String[] strings = line.split("[|]");
				if (strings[2].equals("7")) { //7 代表上线
					BIDR bidr = new BIDR();
					bidr.setAAA_login_name(strings[0].substring(1));
					bidr.setLogin_date(new Date(Long.parseLong(strings[3]) * 1000));
					bidr.setNAS_ip(strings[1]);
					bidr.setLogin_ip(strings[4]);
					map.put(strings[4], bidr);
				} else if (strings[2].equals("8")) { //8代表下线
					BIDR bidr = map.get(strings[4]);
					if (bidr != null) {
						bidr.setLogout_date(new Date(Long.parseLong(strings[3]) * 1000));
						bidr.setTime_duration((int) ((bidr.getLogout_date().getTime() - bidr.getLogin_date().getTime())));
						bidrs.add(bidr);
						map.remove(strings[4]);
					}
				}

			}

			if (reader != null) {
				reader.close();
			}
		} else {
			logger.warn("采集数据的源文件不存在");
		}

		logger.info("对不是同一天上下线的用户进行处理");
		if (map.size() > 0) {
			for (String key : map.keySet()) {
				// 创建Date代表当前时间
				Date date = new Date();
				// 获取map集合里面了不完整的信息
				BIDR bidr = map.get(key);
				BIDR newBidr = new BIDR();
				// 上线时间和当天不是同一天
				if (date.getDate() != bidr.getLogin_date().getDate()) {
					newBidr.setAAA_login_name(bidr.getAAA_login_name());
					newBidr.setLogin_date(bidr.getLogin_date());
					newBidr.setLogin_ip(bidr.getLogin_ip());
					newBidr.setNAS_ip(bidr.getNAS_ip());

					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
					String format = simpleDateFormat.format(date);
					// parse代表某一天中0点的时间
					Date parse = simpleDateFormat.parse(format);
					// 过了0点强制设置下线时间为0点
					newBidr.setLogout_date(parse);
					// 持续时间
					newBidr.setTime_duration(
							(int) (newBidr.getLogout_date().getTime() - newBidr.getLogin_date().getTime()));
					bidrs.add(newBidr);
					// 重新设置bidr的上线时间
					bidr.setLogin_date(parse);

				}
			}
		}
		logger.info("开始备份数据.....");
		backups.store(countFileName, skip, IBackups.STORE_OVERRID);
		backups.store(gatherFileName, map, IBackups.STORE_OVERRID);
		logger.info("此次采集完整数据为:" + bidrs.size());
		logger.info("此次采集不完整数据为:" + map.size());
		return bidrs;
	}

}
