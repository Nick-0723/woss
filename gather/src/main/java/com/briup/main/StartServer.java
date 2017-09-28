package com.briup.main;

import com.briup.common.WossConfiguration;
import com.briup.server.WossServer;
import com.briup.util.BIDR;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;

public class StartServer {
    private static int port = 6666;


    public static void main(String[] args) {
        ServerSocket server = null;
        Socket socket = null;
        try {
            new WossConfiguration().getLogger().info("服务端启动");
            server = new ServerSocket(port);
            while (true) {
                socket = server.accept();
                new ServerThread(socket).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class ServerThread extends Thread{

    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            WossConfiguration wossConfiguration = new WossConfiguration();

            WossServer server = (WossServer) wossConfiguration.getServer();
            server.setSocket(socket);
            Collection<BIDR> bidrs = server.revicer();
            wossConfiguration.getDBStore().saveToDB(bidrs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
