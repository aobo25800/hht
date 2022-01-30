package com.zjz.mq.test;



import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author zjz
 * @date 2022/1/29 11:07
 */
public class Send {
//    public static void main(String[] args) {
//        ConnectionFactory factory = new ConnectionFactory();
//        try {
//            factory.setHost("192.168.0.193");
//            Connection connection = factory.newConnection();
//            Channel channel = connection.createChannel();
//            channel.queueDeclare("dz", true, false, false, null);
//            channel.basicPublish("", "dz", null, "zhaojianzhi".getBytes());
//        }catch (IOException | TimeoutException e) {
//            System.out.println("rabbitmq connect exception");
//        }
//    }
}
