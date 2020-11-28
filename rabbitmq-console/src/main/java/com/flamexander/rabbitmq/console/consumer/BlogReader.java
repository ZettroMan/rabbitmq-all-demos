package com.flamexander.rabbitmq.console.consumer;

import com.flamexander.rabbitmq.console.common.Article;
import com.rabbitmq.client.*;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.TimeoutException;

import static com.flamexander.rabbitmq.console.common.CommonDefs.*;


public class BlogReader {

    private static List<String> subscriptions;
    private static final String EXCHANGER_NAME = "IT-Blog";
    private static Channel channel;
    private static String queueName;

    public static void main(String[] args) throws IOException, TimeoutException {

        subscriptions = new ArrayList<>();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGER_NAME, BuiltinExchangeType.TOPIC);

        // get random queue for this Reader
        queueName = channel.queueDeclare().getQueue();
        System.out.println("My queue name: " + queueName);
        // open file to write received articles
        PrintWriter writer = new PrintWriter(queueName + ".log", "UTF-8");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            Article article = SerializationUtils.deserialize(delivery.getBody());
            writer.write("\n [x] Received article:\n" + article);
            writer.flush();
        };

        // start listening to this queue
        System.out.println(" [*] Waiting for messages");
        displayHelp();
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });


        // binding and unbinding cycle
        Scanner scanner = new Scanner(System.in);
        boolean active = true;
        while (active) {
            displayPrompt();
            String line = scanner.nextLine();
            String[] command = line.split("\\s+", 2);
            switch (command[0]) {
                case "/help":
                    displayHelp();
                    break;
                case "/list":
                    list();
                    break;
                case "/themes":
                    showThemes();
                    break;
                case "/langs":
                    showLanguages();
                    break;
                case "/exit":
                case "/quit":
                    active = false;
                    break;
                default:
                    if (command.length < 2) {
                        System.out.println("Unrecognized command '" + command[0] + "'");
                        break;
                    }
                    if (command[0].equals("/+")) {
                        subscribe(command[1]);
                    } else if (command[0].equals("/-")) {
                        unsubscribe(command[1]);
                    } else {
                        System.out.println("Unrecognized command '" + command[0] + "'");
                    }
            }
        }
        channel.close();
        writer.close();
        System.exit(0);
    }

    private static void displayPrompt() {
        System.out.print("\nPlease, enter a command (/help - for help)  > ");

    }

    private static void showLanguages() {
        System.out.println("====================================");
        System.out.println("Available languages:");
        for (String lang : langs) {
            System.out.println("    " + lang);
        }
    }

    private static void showThemes() {
        System.out.println("====================================");
        System.out.println("Available themes:");
        for (int i = 1; i < themes.length; i++) {
            System.out.println("    " + themes[i]);
        }

    }

    private static void displayHelp() {
        System.out.println("\nAvailable commands:");
        System.out.println("  /help - display this help message");
        System.out.println("  /list - display current subscriptions");
        System.out.println("  /themes - display available themes and categories");
        System.out.println("  /langs - display available themes and categories");
        System.out.println("  /+ [lng][.][th] - subscribe on theme [th] for language [lng]");
        System.out.println("  /- [lng][.][th] - unsubscribe from theme [th] for language [lng]");
        System.out.println("  /exit, /quit  - exit program");
        System.out.println("  For subscription operations it is possible to use wildcards '*' and '#' for language and theme values");
    }


    public static void subscribe(String theme) throws IOException {
        channel.queueBind(queueName, EXCHANGER_NAME, theme);
        subscriptions.add(theme);
        System.out.println("You just subscribed on theme " + theme);
        list();
    }

    public static void unsubscribe(String theme) throws IOException {
        channel.queueUnbind(queueName, EXCHANGER_NAME, theme);
        subscriptions.remove(theme);
        System.out.println("You just unsubscribed from theme " + theme);
        list();
    }

    public static void list() {
        System.out.print("\nCurrent subscriptions: ");
        for (String s : subscriptions) {
            System.out.print("[" + s + "]  ");
        }

    }

}
