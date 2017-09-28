package com.briup.logger;

import com.briup.util.Configuration;
import com.briup.util.Logger;
import com.briup.woss.ConfigurationAWare;
import org.apache.log4j.PropertyConfigurator;

import java.util.Properties;

public class WossLogger implements Logger,ConfigurationAWare {
    private org.apache.log4j.Logger logger;
    @Override
    public void debug(String s) {
        logger.debug(s);
    }

    @Override
    public void info(String s) {
        logger.info(s);
    }

    @Override
    public void warn(String s) {
        logger.warn(s);
    }

    @Override
    public void error(String s) {
        logger.error(s);
    }

    @Override
    public void fatal(String s) {
        logger.fatal(s);
    }

    @Override
    public void init(Properties properties) {
        PropertyConfigurator.configure(properties.getProperty("propertiesFile"));
        logger = org.apache.log4j.Logger.getRootLogger();
    }

    @Override
    public void setConfiguration(Configuration configuration) {

    }
}
