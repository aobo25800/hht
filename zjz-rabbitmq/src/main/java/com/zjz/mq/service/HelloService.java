package com.zjz.mq.service;

import com.zjz.mq.config.RabbitmqConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author zjz
 * @date 2022/1/29 15:04
 */
@Service
public class HelloService {
    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendDelayedMsg(String msg, Integer delay) {
        MessageProperties mp = new MessageProperties();
        // 设置过期时间
        mp.setDelay(delay);
        Message message = new Message(msg.getBytes(), mp);
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_NAME_DELAYED, "DELAY.MSG", message);
        System.out.println("生产消息时间： " + new Date());
    }

    @RabbitListener(queues = {RabbitmqConfig.QUEUE_NAME_DELAYED})
    public void receive(String message) {
        System.out.println("消费消息时间： " + new Date());
        System.out.println(message);
    }
}
