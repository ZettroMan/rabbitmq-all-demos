package com.flamexander.rabbitmq.console.producer;

import com.flamexander.rabbitmq.console.common.Article;
import com.rabbitmq.client.*;
import org.apache.commons.lang3.SerializationUtils;

import java.util.Random;

import static com.flamexander.rabbitmq.console.common.CommonDefs.*;


public class ITBlogger {
    private static final String EXCHANGER_NAME = "IT-Blog";

    public static void main(String[] argv) throws Exception {
        Random random = new Random();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGER_NAME, BuiltinExchangeType.TOPIC);

            while (true) {
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
                boolean upperCase = true;
                int contentSize = 200 + random.nextInt(400);
                for (int j = 0; j < contentSize; j++) {
                    char character = alphabet.charAt(random.nextInt(alphabet.length()));
                    if(upperCase && character!=' ' && character != '\n') {
                        character = Character.toUpperCase(character);
                        upperCase = false;
                    }
                    stringBuilder.append(character);
                    if(character == '\n') {
                        upperCase = true;
                    }
                }
                channel.basicPublish(EXCHANGER_NAME, comboTheme.toString(), null, SerializationUtils.serialize(new Article(comboTheme.toString(), stringBuilder.toString())));
                System.out.println(" [x] Published an article with \"" + comboTheme.toString() + "\" theme");

                // take a pause to write a new article
                Thread.sleep(500 + random.nextInt(1500));
            }
        }
    }
}
