package com.briup.backup.impl;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.briup.backup.IBackups;

/**
 * 
 * Copyright: Copyright (c) 2017 wangzh
 * 
 * @see: Backups.java
 * @Description: 备份模块的实现类,主要对数据进行备份操作
 *
 * @version: v1.0.0
 * @author: wangzh
 * @date: 2017年9月15日 下午3:18:27 
 *
 */
public class Backups implements IBackups {
	private String path; //备份数据存在的路径
	private Logger logger = Logger.getRootLogger(); //获取日志

	@Override
	public void init(Properties properties) {
		path = properties.getProperty("path"); //初始化path
	}

	/**
	 * 备份数据
	 */
	public void store(String key, Object object, boolean flag) {
		try {
			//如果要备份的数据文件的名字为空直接返回
			if (StringUtils.isBlank(key)) {
				logger.info("备份数据时文件名字为null");
				return;
			}
			/*
			 * 1.创建文件，文件如果不存在，则直接创建一个
			 * 2.构建对象输出流，将数据写入到文件中
			 */
			File file = new File(path, key);
			if (!file.exists()) {
				file.createNewFile();
			}
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file, flag));
			out.writeObject(object);
			out.flush();
			out.close();
		} catch (Exception e) {
			//对异常进行处理
			logger.error("备份数据失败....", e);
			System.exit(-1);
		}
	}

	/**
	 * 加载备份数据
	 */
	public Object load(String key, boolean flag) {
		try {
			System.out.println(path+key);
			File file = new File(path, key); // 构建文件
			if (!file.exists()) { // 如果文件不存在那么就结束
				logger.info("load方法中根据key值不存在备份文件");
				return null;
			}
			Object object = null;
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
			object = inputStream.readObject();
			inputStream.close();
			if (LOAD_REMOVE == flag) { // 如果读取数据需要将备份文件删除
				file.delete();
			}
			return object;
		} catch (Exception e) {
			//对异常进行处理
			logger.warn("读取备份数据失败",e);
			return null;
		}
	}

}
