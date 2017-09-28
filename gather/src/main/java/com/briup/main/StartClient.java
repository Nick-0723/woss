package com.briup.main;

import com.briup.client.WossClient;
import com.briup.client.WossGather;
import com.briup.common.WossConfiguration;
import com.briup.util.Logger;
import com.briup.woss.client.Client;

public class StartClient {

    public static void main(String[] args) {
        try {
            WossConfiguration wossConfiguration = new WossConfiguration();
            Logger logger = wossConfiguration.getLogger();
            logger.info("客户端启动");
            Client client = wossConfiguration.getClient();
            client.send(((WossClient)client).getBidrs());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
