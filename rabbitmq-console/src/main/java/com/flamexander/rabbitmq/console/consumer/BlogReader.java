package com.flamexander.rabbitmq.console.consumer;

import com.flamexander.rabbitmq.console.common.Article;
import com.rabbitmq.client.*;
import org.apache.commons.lang3.SerializationUtils;

import java.util.*;

public class BlogReader {

    private Map<String, Thread> subscriptions;
    private static final String EXCHANGE_NAME = "IT-Blog";

    public BlogReader() {
        this.subscriptions = new HashMap<>();
    }

    public void subscribe(String theme) {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            Article article = SerializationUtils.deserialize(delivery.getBody());
            System.out.println(" [x] Received article:\n" + article);
        };
        Thread thread = new Thread(() -> {
            try {
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost("localhost");
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();

                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC, false, true, null);
                String queueName = channel.queueDeclare().getQueue();
                System.out.println("My queue name: " + queueName);
                channel.queueBind(queueName, EXCHANGE_NAME, theme);
                System.out.println(" [*] Waiting for messages");

                channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        subscriptions.put(theme, thread);
        thread.start();
    }

    public void unsubscribe(String theme) {
        Thread thread = subscriptions.get(theme);
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }
}
