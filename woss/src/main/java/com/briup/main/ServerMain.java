package com.briup.main;

import org.apache.log4j.Logger;

import com.briup.common.IConfiguration;
import com.briup.common.impl.ConfigurationImpl;

public class ServerMain {
	private static  Logger logger = Logger.getLogger("serverLogger");

	public static void main(String[] args) {
		try {
			//接受数据
			IConfiguration configuration = new ConfigurationImpl();
			configuration.getServer().recive();;
		} catch (Exception e) {
			logger.error("系统启动报错",e);
			System.exit(-1);
		}
	}
}
