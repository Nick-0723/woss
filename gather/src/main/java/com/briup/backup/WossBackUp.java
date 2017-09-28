package com.briup.backup;

import com.briup.util.BackUP;
import com.briup.util.Configuration;
import com.briup.util.Logger;
import com.briup.woss.ConfigurationAWare;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class WossBackUp implements BackUP,ConfigurationAWare{
    private Logger logger;
    private Map<String,Object> bak;
    private String path;
    @Override
    public void store(String s, Object o, boolean b) throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream(path+s,b);
        ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
        out.writeObject(o);
        out.flush();
        out.close();
        logger.info(path+s+"备份完成");
    }

    @Override
    public Object load(String s, boolean b) throws Exception {
        Object o = null;
        File file = new File(path,s);
        if(file.exists()){
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            o = in.readObject();
            if(b){
                new File(path+s).delete();
                logger.info(path+s+"删除成功");
            }
        }else{
            logger.error(path+s+"文件不存在");
        }
        logger.info("加载完成");
        return o;
    }

    @Override
    public void init(Properties properties) {
        bak = new HashMap<>();
        path = properties.getProperty("path");
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        try {
            logger = configuration.getLogger();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
