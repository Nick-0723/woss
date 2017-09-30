package com.briup.common;

import com.briup.util.BackUP;
import com.briup.util.Configuration;
import com.briup.util.Logger;
import com.briup.woss.ConfigurationAWare;
import com.briup.woss.WossModule;
import com.briup.woss.client.Client;
import com.briup.woss.client.Gather;
import com.briup.woss.server.DBStore;
import com.briup.woss.server.Server;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.List;
import java.util.Properties;

/**
 * 配置模块
 * @author Nick
 */
@SuppressWarnings("all")
public class WossConfiguration implements Configuration {
    private WossModule logger,backUP,gather,client,server,dbStore;

    private Document document;
    private Properties properties;
    private WossModule[] wossModules;


    public WossConfiguration() throws Exception {
        SAXReader saxReader = new SAXReader();
        document = saxReader.read("gather/src/main/resources/configuration.xml");
        properties = new Properties();
        wossModules = new WossModule[]{logger,gather,client,dbStore,server,backUP};
        handler();
    }

    /**
     * 处理所有模块的xml文件,把需要的东西注入到properties对象中
     * @throws Exception
     */
    private void handler() throws Exception {
        Element rootElement = document.getRootElement();
        List elements = rootElement.elements();
        Object[] ini = new Object[]{logger,gather,client,dbStore,server,backUP};
        for (int i = 0; i < elements.size(); i++){
            Element element = (Element) elements.get(i);
            ini[i] = Class.forName(element.attributeValue("class")).newInstance();
            wossModules[i] = (WossModule) ini[i];
            switch (element.getQName().getName()){
                case "logger":
                    setProperties(element,"propertiesFile");
                    break;
                case "gather":
                    setProperties(element,"sourceData","canliushujuFile");
                    break;
                case "client":
                    setProperties(element,"host","port","tempFile");
                    break;
                case "dbStore":break;
                case "server":
                    setProperties(element,"server_tempFile");
                    break;
                case "backUP":
                    setProperties(element,"path");
                    break;
            }
        }
        for (int j = 0; j < ini.length; j++) {
            ((WossModule)ini[j]).init(properties);
            ((ConfigurationAWare)ini[j]).setConfiguration(this);
        }
    }

    private void setProperties(Element element,String...name){
        for (int i = 0; i < name.length; i++) {
            String temp = element.element(name[i]).getTextTrim();
            properties.setProperty(name[i],temp);
        }
    }


    /**
     * 获取Logger对象
     * @return Logger对象
     * @throws Exception
     */
    @Override
    public Logger getLogger() throws Exception {
        return (Logger) wossModules[0];
    }

    /**
     * 获取Gather对象
     * @return Gather对象
     * @throws Exception
     */
    @Override
    public Gather getGather() throws Exception {
        return (Gather)wossModules[1];
    }

    /**
     * 获取Client对象
     * @return Client对象
     * @throws Exception
     */
    @Override
    public Client getClient() throws Exception {
        return (Client)wossModules[2];
    }

    /**
     * 获取DBStore对象
     * @return DBStore对象
     * @throws Exception
     */
    @Override
    public DBStore getDBStore() throws Exception {
        return (DBStore)wossModules[3];
    }

    /**
     * 获取Server对象
     * @return Server对象
     * @throws Exception
     */
    @Override
    public Server getServer() throws Exception {
        return (Server)wossModules[4];
    }

    /**
     * 获取BackUP对象
     * @return BackUP对象
     * @throws Exception
     */
    @Override
    public BackUP getBackup() throws Exception {
        return (BackUP)wossModules[5];
    }

}
