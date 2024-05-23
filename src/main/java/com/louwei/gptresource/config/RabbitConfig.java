package com.louwei.gptresource.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单延迟队列
 */
@Configuration
public class RabbitConfig {
//    private static final String ORDER_EXCHANGE = "order_exchange";
//    private static final String ORDER_QUEUE = "order_queue";
    private static final String DEAD_EXCHANGE = "dead_exchange";
    private static final String DEAD_QUEUE = "dead_queue";
    private static final String DELAYED_EXCHANGE = "token_delayed_exchange";
    private static final String DELAYED_QUEUE = "token_delayed_queue";
    private static final String CHUNK_DELAYED_EXCHANGE = "chunk_delayed_exchange";
    private static final String CHUNK_DELAYED_QUEUE = "chunk_delayed_queue";
    private static final String ORDER_DELAYED_EXCHANGE = "order_delayed_exchange";
    private static final String ORDER_DELAYED_QUEUE = "order_delayed_queue";


//    @Bean
//    public ConnectionFactory connectionFactory() {
//        ConnectionFactory connectionFactory = new ConnectionFactory();
//        connectionFactory.setHost("49.235.137.135");
//        connectionFactory.setPort(5672); // 默认端口
//        connectionFactory.setUsername("muhuo");
//        connectionFactory.setPassword("muhuo");
//        connectionFactory.setVirtualHost("/");
//        return connectionFactory;
//    }

    //1.延迟交换机
    @Bean(DELAYED_EXCHANGE)
    public Exchange delayedExchange() {
         // 创建自定义交换机
         Map<String, Object> args = new HashMap<>();
         args.put("x-delayed-type", "topic"); // topic类型的延迟交换机
         return new CustomExchange(DELAYED_EXCHANGE, "x-delayed-message", true, false, args);
    }

    @Bean(DELAYED_QUEUE)
    public Queue delayedQueue() {
        return QueueBuilder
                .durable(DELAYED_QUEUE)
                .deadLetterExchange(DEAD_EXCHANGE)
                .deadLetterRoutingKey("token_dead_routing")
                .ttl(50000)
                .maxLength(10)
                .build();
    }

    @Bean
    public Binding delayedBing(@Qualifier(DELAYED_EXCHANGE) Exchange exchange,@Qualifier(DELAYED_QUEUE) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("token_routing")
                .noargs();
    }

    @Bean(ORDER_DELAYED_EXCHANGE)
    public Exchange orderDelayedExchange() {
        // 创建自定义交换机
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "topic"); // topic类型的延迟交换机
        return new CustomExchange(ORDER_DELAYED_EXCHANGE, "x-delayed-message", true, false, args);
    }

    @Bean(ORDER_DELAYED_QUEUE)
    public Queue orderDelayedQueue() {
        return QueueBuilder
                .durable(ORDER_DELAYED_QUEUE)
                .deadLetterExchange(DEAD_EXCHANGE)
                .deadLetterRoutingKey("order_dead_routing")
                .ttl(100000)
                .maxLength(100)
                .build();
    }

    @Bean
    public Binding orderDelayedBinding(@Qualifier(ORDER_DELAYED_EXCHANGE) Exchange exchange,@Qualifier(ORDER_DELAYED_QUEUE) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("order_routing")
                .noargs();
    }

    /**
     * 会员过期
     * @return
     */

    @Bean(CHUNK_DELAYED_EXCHANGE)
    public Exchange chunkDelayedExchange() {
        // 创建自定义交换机
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "topic"); // topic类型的延迟交换机
        return new CustomExchange(CHUNK_DELAYED_EXCHANGE, "x-delayed-message", true, false, args);
    }

    @Bean(CHUNK_DELAYED_QUEUE)
    public Queue chunkDelayedQueue() {
        return QueueBuilder
                .durable(CHUNK_DELAYED_QUEUE)
                .deadLetterExchange(DEAD_EXCHANGE)
                .deadLetterRoutingKey("chunk_dead_routing")
                .ttl(100000)
                .maxLength(100)
                .build();
    }

    @Bean
    public Binding chunkDelayedBinding(@Qualifier(CHUNK_DELAYED_EXCHANGE) Exchange exchange,@Qualifier(CHUNK_DELAYED_QUEUE) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("chunk_routing")
                .noargs();
    }

//    @Bean("orderExchange")
//    public Exchange getExchange() {
//        return ExchangeBuilder
//                .topicExchange(ORDER_EXCHANGE)
//                .durable(true)
//                .build();
//    }

//    @Bean("orderQueue")
//    public Queue getQueue() {
//        return QueueBuilder
//                .durable(ORDER_QUEUE)
//                .ttl(10000)
//                .deadLetterExchange(EXPIRE_EXCHANGE)
//                .deadLetterRoutingKey("expire_routing")
//                .build();
//    }
//
    @Bean(DEAD_EXCHANGE)
    public Exchange getDeadExchange() {
        return ExchangeBuilder
                .topicExchange(DEAD_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean(DEAD_QUEUE)
    public Queue getDeadQueue() {
        return new Queue(DEAD_QUEUE);
    }

    @Bean
    public Binding bingExpireMessage(@Qualifier(DEAD_EXCHANGE) Exchange exchange,@Qualifier(DEAD_QUEUE) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("*._dead_routing")
                .noargs();
    }

//    @Bean
//    public Binding bingMessage(@Qualifier("orderExchange") Exchange exchange,@Qualifier("orderQueue") Queue queue) {
//        return BindingBuilder
//                .bind(queue)
//                .to(exchange)
//                .with("order_routing")
//                .noargs();
//    }

    @Bean
    public SimpleRabbitListenerContainerFactory batchListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(5);  // 并发消费数量
        factory.setMaxConcurrentConsumers(10); // 最大并发消费数量
        factory.setBatchSize(10);  // 每次接收的消息数量
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 手动签收
        return factory;
    }
}
