package com.briup.common;

import com.briup.backup.WossBackUp;
import com.briup.client.WossClient;
import com.briup.client.WossGather;
import com.briup.logger.WossLogger;
import com.briup.server.WossDBStore;
import com.briup.server.WossServer;
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
import org.junit.Test;

import java.util.List;
import java.util.Properties;

public class WossConfiguration implements Configuration {
    private WossLogger logger;
    private WossBackUp backUP;
    private WossGather gather;
    private WossClient client;
    private WossServer server;
    private WossDBStore dbStore;

    private Document document;
    private Properties properties;

    public WossConfiguration() throws Exception {
        SAXReader saxReader = new SAXReader();
        document = saxReader.read("gather/src/main/resources/configuration.xml");
        properties = new Properties();
        handler();
    }

    @Test
    public void handler() throws Exception {
        Element rootElement = document.getRootElement();
        List elements = rootElement.elements();
        for (int i = 0; i < elements.size(); i++){
            Element element = (Element) elements.get(i);
            switch (element.getQName().getName()){
                case "gather":
                    gather = (WossGather) Class.forName(element.attributeValue("class")).newInstance();
                    setProperties(element,"sourceData","canliushujuFile");
                    break;
                case "client":
                    client = (WossClient) Class.forName(element.attributeValue("class")).newInstance();
                    setProperties(element,"host","port","tempFile");
                    break;
                case "server":
                    server = (WossServer) Class.forName(element.attributeValue("class")).newInstance();
                    setProperties(element,"server_tempFile");
                    break;
                case "backUp":
                    backUP = (WossBackUp) Class.forName(element.attributeValue("class")).newInstance();
                    setProperties(element,"path");
                    break;
                case "dbStore":
                    dbStore = (WossDBStore) Class.forName(element.attributeValue("class")).newInstance();
                    break;
                case "logger":
                    logger = (WossLogger) Class.forName(element.attributeValue("class")).newInstance();
                    setProperties(element,"propertiesFile");
                    break;
            }
        }
        Object[] ini = new Object[]{gather,server,client,backUP,logger,dbStore};
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


    @Override
    public Logger getLogger() throws Exception {

        return logger;
    }

    @Override
    public BackUP getBackup() throws Exception {
        return backUP;
    }

    @Override
    public Gather getGather() throws Exception {
        return gather;
    }

    @Override
    public Client getClient() throws Exception {
        return client;
    }

    @Override
    public Server getServer() throws Exception {
        return server;
    }

    @Override
    public DBStore getDBStore() throws Exception {
        return dbStore;
    }
}
