package com.briup.server.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.briup.backup.IBackups;
import com.briup.bean.BIDR;
import com.briup.common.IConfiguration;
import com.briup.common.IConfigurationAWare;
import com.briup.server.IDBStore;

public class DBStoreImpl implements IDBStore, IConfigurationAWare {
	private String driverName; // 驱动类名
	private String url; // 连接数据库 url
	private String user; // 用户名
	private String password; // 密码
	private String bakFileName; // 备份文件的名字
	private IBackups backups; // 备份文件
	private PreparedStatement ps;
	private Logger logger = Logger.getLogger("serverLogger");

	/**
	 * 初始化数据
	 */
	@Override
	public void init(Properties properties) {
		driverName = properties.getProperty("driverName");
		url = properties.getProperty("url");
		user = properties.getProperty("user");
		password = properties.getProperty("password");
		bakFileName = properties.getProperty("bakFileName");
	}

	/**
	 * 初始化备份模快数据
	 */
	@Override
	public void setConfiguration(IConfiguration configuration) {
		backups = configuration.getBackups();
	}

	@Override
	public void save(Collection<BIDR> collection) throws Exception {
		logger.info("开始入库....");
		Connection connection = getConnection(); // 获取连接
		connection.setAutoCommit(false); // 设置否定的自动提交
		int batch = 0; // 用来记录ps中有多少条sql语句
		int day = 0; // 对应着某一天的表
		logger.info("加载备份数据");
		Collection<BIDR> bidrs = (Collection<BIDR>) backups.load(bakFileName, IBackups.LOAD_REMOVE);
		if (bidrs != null) {
			collection.addAll(bidrs);
		}
		try {
			//遍历集合
			for (BIDR bidr : collection) {
				batch++;
				if (day != bidr.getLogin_date().getDate()) {
					day = bidr.getLogin_date().getDate(); // 26
					if (ps != null) {
						ps.executeBatch();
						ps.close();
					}
					String sql = "insert into t_detail_" + day + " values(?,?,?,?,?,?)";
					ps = connection.prepareStatement(sql);
				}
				ps.setString(1, bidr.getAAA_login_name());
				ps.setString(2, bidr.getLogin_ip());
				ps.setDate(3, new Date(bidr.getLogin_date().getTime()));
				ps.setDate(4, new Date(bidr.getLogout_date().getTime()));
				ps.setString(5, bidr.getNAS_ip());
				ps.setInt(6, bidr.getTime_duration());
				ps.addBatch();

				if (batch % 200 == 0 || batch == collection.size()) {
					ps.executeBatch();
				}

			}
			connection.commit();
			System.out.println("传输完成");
		} catch (Exception e) {
			logger.warn("数据入库失败..", e);
			connection.rollback();
			backups.store(bakFileName, collection, IBackups.STORE_OVERRID); // 若事务回滚，需要把数据备份起来
		}

	}

	/**
	 * 获取连接
	 */
	public Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName(driverName);
			connection = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
		return connection;
	}

}
