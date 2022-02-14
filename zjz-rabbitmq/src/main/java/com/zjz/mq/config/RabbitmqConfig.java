package com.zjz.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zjz
 * @date 2022/1/29 14:53
 */
@Configuration
public class RabbitmqConfig {

//    @Value("${spring.rabbitmq.template.default-receive-queue}")
//    private String queue;
//    @Value("${spring.rabbitmq.template.exchange}")
//    private String exchange;
//    @Value("${spring.rabbitmq.template.routing-key}")
//    private String routingKey;
//
//    @Bean
//    public TopicExchange getExchangeName() {
//        return new TopicExchange(exchange);
//    }
//
//    @Bean
//    public Queue getQueueName() {
//        return new Queue("dz.queue");
//    }
//
//    @Bean
//    public Queue getQueueName1() {
//        return new Queue("dz.queue.test");
//    }
//
//    @Bean
//    public Binding declareBinding() {
//        return BindingBuilder.bind(getQueueName()).to(getExchangeName())
//                .with(routingKey);
//    }
//
//    @Bean
//    public Binding declareBinding1() {
//        return BindingBuilder.bind(getQueueName1()).to(getExchangeName())
//                .with(routingKey);
//    }
//
//    @Bean
//    public Jackson2JsonMessageConverter getMessageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }
//
//    @Bean
//    public RabbitTemplate rabbitTemplate(final ConnectionFactory factory) {
//        final RabbitTemplate rabbitTemplate = new RabbitTemplate(factory);
//        rabbitTemplate.setMessageConverter(getMessageConverter());
//        return rabbitTemplate;
//    }
//
//    @Bean
//    public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
//        return new MappingJackson2MessageConverter();
//    }
//
//    @Bean
//    public DefaultMessageHandlerMethodFactory messageHandlerMethodFactory() {
//        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
//        factory.setMessageConverter(consumerJackson2MessageConverter());
//        return factory;
//    }
//
//    public String getRoutingKey() {
//        return routingKey;
//    }
//
////    public String getQueue() {
////        return queue;
////    }
//
//    public String getExchange() {
//        return exchange;
//    }

    public static final String QUEUE_NAME_DELAYED = "DELAY.QUEUE";
    public static final String EXCHANGE_NAME_DELAYED = "DELAY.EXCHANGE";
    public static final String ROUTING_KEY_DELAYED = "DELAY.#";

    @Bean(QUEUE_NAME_DELAYED)
    public Queue queue() {
        return QueueBuilder.durable(QUEUE_NAME_DELAYED).build();
    }

    @Bean(EXCHANGE_NAME_DELAYED)
    public CustomExchange exchange() {
        Map<String, Object> arguments = new HashMap<>(1);
        arguments.put("x-delayed-type", "topic");
        return new CustomExchange(EXCHANGE_NAME_DELAYED, "x-delayed-message", true, false, arguments);
    }

    @Bean
    public Binding bindingNotify(@Qualifier(QUEUE_NAME_DELAYED) Queue queue, @Qualifier(EXCHANGE_NAME_DELAYED) CustomExchange customExchange) {
        return BindingBuilder.bind(queue).to(customExchange).with(ROUTING_KEY_DELAYED).noargs();
    }

}
