package com.briup.test;

import com.briup.main.StartClient;

import java.util.Random;

public class Test1 {
    public static void main(String[] args) throws InterruptedException {
        while (true){
            StartClient.main(null);
            Thread.sleep(new Random().nextInt(30000));
        }
    }
}
