package com.zjz.mq.service;

import com.zjz.mq.config.RabbitmqConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zjz
 * @date 2022/1/29 15:04
 */
@Service
public class HelloService {
    @Resource
    private RabbitmqConfig rabbitmqConfig;
    @Resource
    private RabbitTemplate rabbitTemplate;

    public String func(String data) {
        rabbitTemplate.convertAndSend(rabbitmqConfig.getExchange(), rabbitmqConfig.getRoutingKey(), data);
//        rabbitTemplate.convertAndSend(rabbitmqConfig.getQueue(), data);
        return data;
    }

//    @RabbitListener(queues = "dz.queue")
//    public void receive(String message) {
//        System.out.println(message);
//    }
}
