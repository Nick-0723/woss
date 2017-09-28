package com.briup.common.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.briup.backup.IBackups;
import com.briup.bean.IWossModuleInit;
import com.briup.client.IClient;
import com.briup.client.IGather;
import com.briup.common.IConfiguration;
import com.briup.common.IConfigurationAWare;
import com.briup.logger.ILogger;
import com.briup.server.IDBStore;
import com.briup.server.IServer;

public class ConfigurationImpl implements IConfiguration {
	/**
	 * 各个模块的实例
	 */
	private IBackups backups;
	private IDBStore dbStore;
	private IClient client;
	private IServer server;
	private IGather gather;

	private  Logger log = Logger.getRootLogger(); //日志信息

	public ConfigurationImpl() {
		this("woss/src/main/java/config.xml");
	}
	
	public ConfigurationImpl(String path) {
		SAXReader reader = new SAXReader(); //构建解析器
		try {
			Document document = reader.read(new File(path)); //解析配置文件
			Element root = document.getRootElement(); //获取配置文件的根标签
			List<Element> childElements = root.elements(); //获取根标签下的所有子标签
			Map<String, IWossModuleInit> map = new HashMap<>(); //存放实例对象
			for (Element element : childElements) {
				String value = element.attribute("class").getValue();
				IWossModuleInit init = (IWossModuleInit) Class.forName(value).newInstance(); //获取模块实例
				map.put(element.getName(), init); //存放实例类,key值为xml文件中元素的名字，value为对应的实例
				Properties properties = new Properties();
				 List<Element> nextChildelements = element.elements(); //获取子元素的子元素
				for (Element element2 : nextChildelements) {
					String tagName = element2.getName(); //获取子元素名字
					String tagValue = element2.getTextTrim(); //获取子元素的文本
					properties.setProperty(tagName, tagValue);
				}
				init.init(properties);
			}
			if (map.containsKey("server")) {
				server = (IServer) map.get("server");
			}
			if (map.containsKey("gather")) {
				gather = (IGather) map.get("gather");
			}
			if (map.containsKey("client")) {
				client = (IClient) map.get("client");
			}
			if (map.containsKey("dbStore")) {
				dbStore = (IDBStore) map.get("dbStore");
			} 
			if (map.containsKey("backup")) {
				backups = (IBackups) map.get("backup");
			}
			//利用多态性质注入值
			for (IWossModuleInit init : map.values()) {
				if (init instanceof IConfigurationAWare) {
					((IConfigurationAWare)init).setConfiguration(this);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			System.exit(-1);
		}
	}
	
	@Override
	public IBackups getBackups() {
		if (backups == null) {
			log.info("配置化模块中backups为空");
		}
		return backups;
	}

	@Override
	public IDBStore getDBStore() {
		if (dbStore == null) {
			log.info("配置化模块中dbStore为空");
		}
		return dbStore;
	}

	@Override
	public ILogger getLogger() {
		
		return null;
	}

	@Override
	public IServer getServer() {
		if (server == null) {
			log.info("配置化模块中server为空");
		}
		return server;
	}

	@Override
	public IClient getClient() {
		if (client == null) {
			log.info("配置化模块中client为空");
		}
		return client;
	}

	@Override
	public IGather getGather() {
		if (gather == null) {
			log.info("配置化模块中gather为空");
		}
		return gather;
	}

}
