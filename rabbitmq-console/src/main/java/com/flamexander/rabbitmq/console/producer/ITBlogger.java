package com.flamexander.rabbitmq.console.producer;

import com.flamexander.rabbitmq.console.common.Article;
import com.rabbitmq.client.*;
import org.apache.commons.lang3.SerializationUtils;

import java.util.Random;

import static com.flamexander.rabbitmq.console.common.CommonDefs.*;


public class ITBlogger {
    private static final String EXCHANGE_NAME = "IT-Blog";

    public static void main(String[] argv) throws Exception {
        Random random = new Random();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

            for (int i = 0; i < 100; i++) {
                // determine a theme
                int langIdx = random.nextInt(langs.length);
                int themeIdx = random.nextInt(themes.length);
                StringBuilder comboTheme = new StringBuilder(langs[langIdx]);
                if (themeIdx > 0) {
                    comboTheme.append('.');
                    comboTheme.append(themes[themeIdx]);
                }

                // build random article
                StringBuilder stringBuilder = new StringBuilder();
                for (int j = 0; j < random.nextInt(400); j++) {
                    stringBuilder.append(alphabet.charAt(random.nextInt(alphabet.length())));
                }
                channel.basicPublish(EXCHANGE_NAME, comboTheme.toString(), null, SerializationUtils.serialize(new Article(stringBuilder.toString())));
                System.out.println(" [x] Published an article with \"" + comboTheme.toString() + "\" theme");
                // take a pause to write a new article
                try {
                    Thread.sleep(1000 + random.nextInt(4000));
                } catch (InterruptedException _ignored) {
                    Thread.currentThread().interrupt();
                }
            }

        }
    }
}
