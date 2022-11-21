package com.rabbitmq.learn.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {

    public static final String X_EXCHANGE = "X";
    public static final String QUEUE_A = "QA";
    public static final String QUEUE_B = "QB";
    public static final String Y_DEAD_LETTER_EXCHANGE = "Y";
    public static final String DEAD_LETTER_QUEUE = "QD";

    @Bean("xExchange")
    public DirectExchange exchangeX() {
        return new DirectExchange(X_EXCHANGE);
    }

    @Bean("yExchange")
    public DirectExchange exchangeY() {
        return new DirectExchange(Y_DEAD_LETTER_EXCHANGE);
    }


    @Bean("aQueue")
    public Queue queueA() {
        Map<String, Object> params = new HashMap<>();
        //声明死信交换机
        params.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        //声明死信routing-key
        params.put("x-dead-letter-routing-key", "YD");
        //声明队列的 TTL
        params.put("x-message-ttl", 10000);
        return QueueBuilder.durable(QUEUE_A).withArguments(params).build();
    }

    @Bean("bQueue")
    public Queue queueB() {
        Map<String, Object> params = new HashMap<>();
        //声明死信交换机
        params.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        //声明死信routing-key
        params.put("x-dead-letter-routing-key", "YD");
        //声明队列的 TTL
        params.put("x-message-ttl", 40000);
        return QueueBuilder.durable(QUEUE_B).withArguments(params).build();
    }

    @Bean("dQueue")
    public Queue queueD() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public Binding queueaBindingX(@Qualifier("aQueue") Queue aQueue, @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(aQueue).to(xExchange).with("XA");
    }

    @Bean
    public Binding queuebBindingX(@Qualifier("bQueue") Queue bQueue, @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(bQueue).to(xExchange).with("XB");
    }

    @Bean
    public Binding queuedBindingY(@Qualifier("dQueue") Queue dQueue, @Qualifier("yExchange") DirectExchange yExchange) {
        return BindingBuilder.bind(dQueue).to(yExchange).with("YD");
    }

    /**
     * 优化版本
     *
     * @return
     */
    public static final String QUEUE_C = "QC";

    @Bean("cQueue")
    public Queue queueC() {
        Map<String, Object> params = new HashMap<>(2);
        //声明死信交换机
        params.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        //声明死信routing-key
        params.put("x-dead-letter-routing-key", "YD");
        return QueueBuilder.durable(QUEUE_C).withArguments(params).build();
    }

    @Bean
    public Binding queuecBindingX(@Qualifier("cQueue") Queue cQueue, @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(cQueue).to(xExchange).with("XC");
    }

    /**
     * 使用延迟插件优化
     */
    public static final String DELAYED_QUEUE_NAME = "delayed.queue";
    public static final String DELAYED_EXCHANGE_NAME = "delayed.exchange";
    public static final String DELAYED_ROUTING_KEY = "delayed.routingkey";

    @Bean("delayedExchange")
    public CustomExchange delayedExchange() {
        Map params = new HashMap<String, Object>();
        //自定义交换机的类型
        params.put("x-delayed-type", "direct");
        return new CustomExchange(DELAYED_EXCHANGE_NAME, "x-delayed-message", true, false, params);
    }

    @Bean("delayed_queue")
    public Queue delayedQueue() {
        return new Queue(DELAYED_QUEUE_NAME);
    }

    @Bean
    public Binding bindingDelayQueue(@Qualifier("delayed_queue") Queue queue, @Qualifier("delayedExchange") CustomExchange customExchange) {
        return BindingBuilder.bind(queue).to(customExchange).with(DELAYED_ROUTING_KEY).noargs();
    }


}
