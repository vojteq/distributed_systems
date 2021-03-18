package zad_dom;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Admin {

    private static String adminId;
    private static Connection connection;
    private static Channel messageChannel;

    public static void main(String[] args) throws Exception {
        adminId = "admin" + Math.abs(new Random().nextInt() % 100);

        ConnectionFactory factory = new ConnectionFactory();
        connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(Utils.TOPIC_EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        new Thread(new MessageReceiver()).start();

        boolean connected = true;
        while (connected) {
            System.out.println("Who do u want to message ('a' - all, 'ap' - all providers, 'at' - all teams)");
            String receiver = reader.readLine();
            if (receiverIsValid(receiver)) {
                if (receiver.equals("e")) {
                    System.out.println("CLOSING");
                    connected = false;
                    channel.close();
                    messageChannel.close();
                    connection.close();
                } else {
                    System.out.println("Enter your message");
                    String message = reader.readLine();
                    if (!message.isBlank()) {
                        String messageToBeSent = "(" + adminId + "): " + message;
                        String key;
                        switch (receiver) {
                            case "a" -> key = "all";
                            case "ap" -> key = "all.providers";
                            case "at" -> key = "all.teams";
                            default -> key = "";
                        }
                        channel.basicPublish(Utils.TOPIC_EXCHANGE_NAME, key, null, messageToBeSent.getBytes());
                    }
                }
            }
        }
    }

    private static boolean receiverIsValid(String receiver) {
        return receiver.equals("a") || receiver.equals("ap") || receiver.equals("at") || receiver.equals("e");
    }

    private static class MessageReceiver implements Runnable {

        private Consumer consumer;

        @Override
        public void run() {
            try {
                init();
                messageChannel.basicConsume(adminId, true, consumer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void init() throws IOException {
            messageChannel = connection.createChannel();
            messageChannel.exchangeDeclare(Utils.TOPIC_EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
            messageChannel.queueDeclare(adminId, false, true, true, null);
            messageChannel.queueBind(adminId, Utils.TOPIC_EXCHANGE_NAME, "teams.*");
            messageChannel.queueBind(adminId, Utils.TOPIC_EXCHANGE_NAME, "providers.*");

            consumer = new DefaultConsumer(messageChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println(new String(body, StandardCharsets.UTF_8));
                }
            };
        }
    }
}
