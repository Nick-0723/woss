package com.briup.test;

import com.briup.common.WossConfiguration;
import com.briup.logger.WossLogger;
import com.briup.util.Logger;

import java.io.*;

public class test {

    public static void main(String[] args) throws Exception {
        Logger logger = new WossConfiguration().getLogger();
        try {
            BufferedReader br = new BufferedReader(new FileReader("gather/data/radwtmp_test"));
            String data = null;
            boolean flag = true;
            int count = 0;
            while(flag){
                PrintWriter out = new PrintWriter(new FileWriter("gather/data/woss.data",true),true);
                logger.info("第"+count+"次写开始...");
                for (int i = 0; i < 10000; i++) {
                    data = br.readLine();
                    if(data==null){
                        flag = false;
                        break;
                    }
                    Thread.sleep(1);
                    out.println(data);
                }
                out.close();

                logger.info("第"+count+++"次写结束...");
                Thread.sleep(10000);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
