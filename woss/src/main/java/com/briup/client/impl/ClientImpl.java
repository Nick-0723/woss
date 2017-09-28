package com.briup.client.impl;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.briup.backup.IBackups;
import com.briup.bean.BIDR;
import com.briup.client.IClient;
import com.briup.common.IConfiguration;
import com.briup.common.IConfigurationAWare;

public class ClientImpl implements IClient,IConfigurationAWare {
	private String ip; //服务端的ip
	private String port;//端口
	private String bakFileName; //备份文件的名字
	private IBackups backups; //备份模快
	private Logger logger = Logger.getLogger("clientLogger");
	
	/**
	 * 初始化数据
	 */
	@Override
	public void init(Properties properties) {
		ip = properties.getProperty("ip");
		port = properties.getProperty("port");
		bakFileName = properties.getProperty("bakFileName");
	}

	/**
	 * 获取备份模块
	 */
	@Override
	public void setConfiguration(IConfiguration configuration) {
		backups = configuration.getBackups();
	}

	/**
	 * 发送数据的方法
	 */
	@Override
	public void send(Collection<BIDR> collection) {
		try {
			logger.info("进行传输时加载备份数据.....");
			//先加载备份数据
			Collection oldCollection = (Collection) backups.load(bakFileName, IBackups.LOAD_REMOVE);
			//如果备份数据不为空
			if (oldCollection != null) {
				collection.addAll(oldCollection);
			}
			logger.info("开始发送数据.....");
			//创建一个socket对象
			Socket socket = new Socket(ip, Integer.parseInt(port));
			//构建对象输出流
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(collection);
			out.flush();
			out.close();
		} catch(Exception e) {
			//对异常进行处理,如果发送失败则备份数据
			logger.warn("发送数据失败.....",e);
			backups.store(bakFileName, collection, IBackups.STORE_OVERRID);
		}

	}

}
