package com.briup.backup;

import com.briup.util.BackUP;
import com.briup.util.Configuration;
import com.briup.util.Logger;
import com.briup.woss.ConfigurationAWare;

import java.io.*;
import java.util.Properties;

/**
 * 备份模块
 * @author Nick
 */
public class WossBackUp implements BackUP,ConfigurationAWare{
    private Logger logger;
    private String path;


    /**
     * 备份文件
     * @param s 文件名
     * @param o 需要备份的对象
     * @param b 是否追加,true追加,反之不追加
     * @throws Exception
     */
    @Override
    public void store(String s, Object o, boolean b) throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream(path+s,b);
        ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
        out.writeObject(o);
        out.flush();
        out.close();
        logger.info(path+s+"备份完成");
    }

    /**
     * 备份文件还原
     * @param s 文件名
     * @param b 加载完是否删除文件 true删除,反之不删除
     * @return Object对象,从备份文件反序列化出的对象
     * @throws Exception
     */
    @Override
    public Object load(String s, boolean b) throws Exception {
        Object o = null;
        File file = new File(path,s);
        if(file.exists()){
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            o = in.readObject();
            if(b){
                boolean delete = new File(path + s).delete();
                if(delete)
                    logger.info(path+s+"删除成功");
            }
        }else{
            logger.error(path+s+"文件不存在");
        }
        logger.info("加载完成");
        return o;
    }

    /**
     * 初始化方法
     * @param properties
     */
    @Override
    public void init(Properties properties) {
        path = properties.getProperty("path");
    }

    /**
     * 获取配置模块
     * @param configuration
     */
    @Override
    public void setConfiguration(Configuration configuration) {
        try {
            logger = configuration.getLogger();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
