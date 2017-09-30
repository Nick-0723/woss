package com.briup.server;

import com.briup.common.DBUtils;
import com.briup.common.WossConfiguration;
import com.briup.util.BIDR;
import com.briup.util.BackUP;
import com.briup.util.Configuration;
import com.briup.util.Logger;
import com.briup.woss.ConfigurationAWare;
import com.briup.woss.server.DBStore;

import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

public class WossDBStore implements DBStore,ConfigurationAWare {
    private Logger logger;
    private BackUP backUP;

    @Override
    public void saveToDB(Collection<BIDR> collection) throws Exception {
        Iterator<BIDR> iterator = collection.iterator();

        logger.info("准备往数据库发送"+collection.size()+"条数据");
        logger.fatal(collection.size()+"");
//        while (iterator.hasNext()){
//            BIDR bidr = iterator.next();
//            String sql = "insert into t_detail_1 values(?,?,?,?,?,?)";
//            DBUtils.update(sql,bidr.getAAA_login_name(),
//                                bidr.getLogin_ip(),
//                                bidr.getLogin_date(),
//                                bidr.getLogout_date(),
//                                bidr.getNAS_ip(),
//                                bidr.getTime_deration());
//        }

        logger.info("入库成功");
    }

    @Override
    public void init(Properties properties) {

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
