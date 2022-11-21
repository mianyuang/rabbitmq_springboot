package com.rabbitmq.learn.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.learn.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class DeadLetterQueueConsumer {
    @RabbitListener(queues = RabbitConfig.DEAD_LETTER_QUEUE)
    public void receiveD(Message message, Channel channel) {
        String msg = new String(message.getBody());
        log.info("1->当前时间：{},收到死信队列信息{}", new Date().toString(), msg);
    }

    @RabbitListener(queues = RabbitConfig.DELAYED_QUEUE_NAME)
    public void receiveD1(Message message, Channel channel) {
        String msg = new String(message.getBody());
        log.info("2->当前时间：{},收到延迟队列信息{}", new Date().toString(), msg);
    }
}
