package com.briup.client;

import com.briup.util.BIDR;
import com.briup.util.BackUP;
import com.briup.util.Configuration;
import com.briup.util.Logger;
import com.briup.woss.ConfigurationAWare;
import com.briup.woss.client.Gather;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;


public class WossGather implements Gather,ConfigurationAWare{
    private String sourceData_path;
    private Logger logger;
    private BackUP backUP;
    private String canliushujuFile;
//    BIDR 类
//    AAA_Login_name	String
//    Login_ip	String
//    login_date	java.sql.Timestamp
//    logout_date	java.sql.Timestamp
//    NAS_ip		String
//    Time_duration	Integer

//    @SuppressWarnings("all")
    @Override
    public Collection<BIDR> gather() throws Exception {
        Map<String,BIDR> semi = new HashMap<>();
        Collection<BIDR> res = new ArrayList<>();
        logger.info("开始加载上次的残留数据");
        Map<String,BIDR> o = (Map<String,BIDR>)backUP.load(canliushujuFile,true);
        if(o!=null) {
            logger.info("上次残留数据大小"+(o).size());
            semi.putAll(o);
        }

        try {
            long pos = 0;
            logger.info("开始采集数据");

            File file = new File(sourceData_path);
            int count = 0;
            boolean flag = true;
            while (true) {
                System.out.println("第"+count+++"次读取文件");
                RandomAccessFile in = new RandomAccessFile(sourceData_path,"r");
                while(true){
                    //            #jerry|060:wKgAOA|7|1204044555|20.1.1.61
//                    in.setLineNumber(line);
                    if(!flag){
                        System.out.println("上次读到"+pos);
                        in.seek(pos);
                        flag = true;
                    }
                    String temp = in.readLine();
                    if (temp == null){
                        pos = in.getFilePointer();//结束前获取当前偏移量
                        break;
                    }
                    String[] strings = temp.split("[|]");
                    if(strings.length!=5) continue;
                    if (strings[2].equals("7")){
                        String key = strings[1]+strings[4];
                        BIDR value = new BIDR(strings[0].substring(1),strings[1],new Timestamp(Long.parseLong(strings[3])),null,strings[4],null);
                        semi.put(key,value);
                    }else if (strings[2].equals("8")){
                        String key = strings[1]+strings[4];
                        Timestamp logout_date = new Timestamp(Long.parseLong(strings[3]));
                        BIDR bidr = semi.get(key);
                        if(bidr==null)continue;
                        Timestamp login_date = bidr.getLogin_date();
                        Integer time_duration = (int) (logout_date.getTime()-login_date.getTime());
                        bidr.setLogout_date(logout_date);
                        bidr.setTime_deration(time_duration);
                        res.add(bidr);
                        semi.remove(key);
                    }
                }
                in.close();
                if(flag = file.renameTo(new File(sourceData_path+"_"+UUID.randomUUID().getLeastSignificantBits()))) break;
                logger.error("文件重命名不成功");
            }
            logger.info("采集完成,本次残留数据大小:"+semi.size());
            logger.info("开始备份残留数据");
            backUP.store(canliushujuFile,semi,false);
        } catch (FileNotFoundException e) {
            logger.error("文件不存在"+e);
            e.printStackTrace();
        } catch (IOException e) {
            logger.error(""+e);
            e.printStackTrace();
        }

        return res;
    }

    @Override
    public void init(Properties properties) {
        sourceData_path = properties.getProperty("sourceData");
        canliushujuFile = properties.getProperty("canliushujuFile");
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        try {
            backUP = configuration.getBackup();
            logger = configuration.getLogger();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
