package com.flamexander.rabbitmq.spring;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SimpleMessageReceiver {
    public void receiveMessage(byte[] message) {
        System.out.println("Received from topic <" + new String(message) + ">");
    }
}