package com.zjz.mq.test;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author zjz
 * @date 2022/1/29 12:00
 */
public class Recv {

//    public static void main(String[] args) {
//        ConnectionFactory factory = new ConnectionFactory();
//        try {
//            factory.setHost("192.168.0.193");
//            Connection connection = factory.newConnection();
//            Channel channel = connection.createChannel();
//            channel.queueDeclare("dz", true, false, false, null);
//
////            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
////                String message = new String(delivery.getBody(), "UTF-8");
////                System.out.println(" [x] Received '" + message + "'");
////            };
////            channel.basicConsume("dz", true, deliverCallback, consumerTag -> { });
//
//            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//                String message = new String(delivery.getBody(), "UTF-8");
//                System.out.println("message is: " + message);
//            };
//            channel.basicConsume("dz", true, deliverCallback, consumerTag -> {});
//
//        }catch (IOException | TimeoutException e) {
//            System.out.println("rabbitmq connect exception");
//        }
//    }
}
