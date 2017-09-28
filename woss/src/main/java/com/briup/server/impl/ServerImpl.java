package com.briup.server.impl;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.briup.common.IConfiguration;
import com.briup.common.IConfigurationAWare;
import com.briup.server.IDBStore;
import com.briup.server.IServer;
import com.briup.server.ServerThread;

public class ServerImpl implements IServer,IConfigurationAWare{
	private String port; //端口
	private IDBStore dbStore; //入库模块
	private ServerSocket serverSocket;
	private Logger logger = Logger.getLogger("serverLogger");
	@Override
	public void init(Properties properties) {
		port = properties.getProperty("port");
	}

	@Override
	public void recive() throws Exception {
		serverSocket = new ServerSocket(Integer.parseInt(port));
		while(true) {
			logger.info("开始接受数据...");
			Socket socket = serverSocket.accept();
			new ServerThread(socket, dbStore).start();
		}
	}

	@Override
	public void shutDown() throws Exception {
		if (serverSocket != null) {
			serverSocket.close();
		}
	}

	@Override
	public void setConfiguration(IConfiguration configuration) {
		dbStore = configuration.getDBStore();
	}

}
