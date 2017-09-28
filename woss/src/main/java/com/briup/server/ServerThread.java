package com.briup.server;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.briup.bean.BIDR;

/**
 * 
 * Copyright: Copyright (c) 2017 wangzh
 * 
 * @see: ServerThread.java
 * @Description: 服务端的线程类
 *
 * @version: v1.0.0
 * @author: wangzh
 * @date: 2017年9月15日 下午4:06:54 
 */
public class ServerThread extends Thread {
	private Socket socket; 
	private IDBStore dbStore;
	private Logger logger = Logger.getLogger("serverLogger");
	public ServerThread(Socket socket, IDBStore dbStore) {
		this.socket = socket;
		this.dbStore = dbStore;
	}

	@Override
	public void run() {
		Collection<BIDR> collection = null;
		try {
			//获取从客户端传过来的数据
			ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			//获取集合
			collection = (Collection<BIDR>) input.readObject();
			input.close();
			socket.close();
			if (collection != null) {
				//入库
				dbStore.save(collection);
			}
		} catch (Exception e) {
			logger.warn(e.getMessage(),e);
			System.exit(-1);
		}
	}

}
