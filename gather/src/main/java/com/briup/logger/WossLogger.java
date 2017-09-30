package com.briup.logger;

import com.briup.util.Configuration;
import com.briup.util.Logger;
import com.briup.woss.ConfigurationAWare;
import org.apache.log4j.PropertyConfigurator;

import java.util.Properties;

/**
 * 日志模块
 */
public class WossLogger implements Logger,ConfigurationAWare {
    private org.apache.log4j.Logger logger;

    /**
     * debug
     * @param s
     */
    @Override
    public void debug(String s) {
        logger.debug(s);
    }

    /**
     * debug
     * @param s
     */
    @Override
    public void info(String s) {
        logger.info(s);
    }

    /**
     * debug
     * @param s
     */
    @Override
    public void warn(String s) {
        logger.warn(s);
    }

    /**
     * debug
     * @param s
     */
    @Override
    public void error(String s) {
        logger.error(s);
    }

    /**
     * debug
     * @param s
     */
    @Override
    public void fatal(String s) {
        logger.fatal(s);
    }

    /**
     * 初始化方法
     * @param properties
     */
    @Override
    public void init(Properties properties) {
        PropertyConfigurator.configure(properties.getProperty("propertiesFile"));
        logger = org.apache.log4j.Logger.getRootLogger();
    }

    /**
     * 设置配置模块
     * @param configuration
     */
    @Override
    public void setConfiguration(Configuration configuration) {}
}
