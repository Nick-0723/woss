package com.briup.server;

import com.briup.common.WossConfiguration;
import com.briup.util.BIDR;
import com.briup.util.BackUP;
import com.briup.util.Configuration;
import com.briup.util.Logger;
import com.briup.woss.ConfigurationAWare;
import com.briup.woss.server.Server;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

public class WossServer implements Server,ConfigurationAWare {
    private  Socket socket;
    private String tempFile;
    private Logger logger;
    private BackUP backUP;

    public WossServer(){}

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public WossServer(Socket socket) {
        this.socket = socket;
    }

    @Override
    public Collection<BIDR> revicer() throws Exception {
        InputStream in = socket.getInputStream();

        logger.info("服务器正在接收数据");
        ObjectInputStream objectInputStream = new ObjectInputStream(in);
        Object o = objectInputStream.readObject();
        logger.info("接收成功");
        Collection<BIDR> bidrs = null;
        if(o instanceof Collection){
            bidrs = (Collection<BIDR>)o;
        }

        //服务端备份
        logger.info("服务端正在备份");
        backUP.store(tempFile,o,false);

        objectInputStream.close();
        in.close();

        return bidrs;
    }

    @Override
    public void shutdown() {}

    @Override
    public void init(Properties properties) {
        tempFile = properties.getProperty("server_tempFile");

    }

    @Override
    public void setConfiguration(Configuration configuration) {
        try {
            logger = configuration.getLogger();
            backUP = configuration.getBackup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
