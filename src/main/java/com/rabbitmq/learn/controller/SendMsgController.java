package com.rabbitmq.learn.controller;

import com.rabbitmq.learn.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RequestMapping("ttl")
@RestController
@Slf4j
public class SendMsgController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("sendMsg/{message}")
    public void sendMsg(@PathVariable String message) {
        log.info("当前时间：{},发送一条信息给两个 TTL 队列:{}", new Date().toString(), message);
        rabbitTemplate.convertAndSend(RabbitConfig.X_EXCHANGE, "XA", "消息来自 ttl 为 10S 的队列：" + message);
        rabbitTemplate.convertAndSend(RabbitConfig.X_EXCHANGE, "XB", "消息来自 ttl 为 40S 的队列：" + message);
    }

    @GetMapping("sendMsg/{message}/{ttl}")
    public void sendMsg(@PathVariable String message, @PathVariable String ttl) {
        log.info("当前时间：{},发送一条信息给一个队列:{}", new Date().toString(), message);
        rabbitTemplate.convertAndSend(RabbitConfig.X_EXCHANGE, "XC", message, msg -> {
            msg.getMessageProperties().setExpiration(ttl);
            return msg;
        });
    }

    @GetMapping("sendDelayMsg/{message}/{ttl}")
    public void sendDelayedMsg(@PathVariable String message, @PathVariable int ttl) {
        log.info("当前时间：{},发送一条信息给一个队列:{}", new Date().toString(), message);
        rabbitTemplate.convertAndSend(RabbitConfig.DELAYED_EXCHANGE_NAME, RabbitConfig.DELAYED_ROUTING_KEY, message, msg -> {
            msg.getMessageProperties().setDelay(ttl);
            return msg;
        });
    }
}
