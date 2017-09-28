package com.briup.main;

import org.apache.log4j.Logger;

import com.briup.common.IConfiguration;
import com.briup.common.impl.ConfigurationImpl;

public class ClientMain {
	private static  Logger logger = Logger.getLogger("clientLogger");
	public static void main(String[] args) {
		try {
			//发送数据
			IConfiguration configuration = new ConfigurationImpl();
			configuration.getClient().send(configuration.getGather().gather());
		} catch (Exception e) {
			logger.error("系统启动报错", e);
			System.exit(-1);
		}
	}
}
