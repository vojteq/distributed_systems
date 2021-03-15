package zad_dom;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

public class Admin {

    public static void main(String[] args) throws Exception {
        String adminId = "admin" + Math.abs(new Random().nextInt() % 100);

        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(Utils.PRODUCT_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("Who do u want to message ('a' - all, 'ap' - all providers, 'at' - all teams)");
            String receiver = reader.readLine();
            if (receiverIsValid(receiver)) {
                System.out.println("Enter your message");
                String message = reader.readLine();
                if (!message.isBlank()) {
                    String messageToBeSent = "(" + adminId + "): " + message;
                    String key;
                    switch (receiver) {
                        case "a" -> key = "all";
                        case "ap" -> key = "all_providers";
                        case "at" -> key = "all_teams";
                        default -> key = "";
                    }
                    channel.basicPublish(Utils.PRODUCT_EXCHANGE_NAME, key, null, messageToBeSent.getBytes());
                }
            }
        }
    }

    private static boolean receiverIsValid(String receiver) {
        return receiver.equals("a") || receiver.equals("ap") || receiver.equals("at");
    }
}
