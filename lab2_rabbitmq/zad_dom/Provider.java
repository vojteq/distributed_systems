package zad_dom;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class Provider {
    public static void main(String[] args) throws Exception {
        String providerId = "provider" + Math.abs(new Random().nextInt() % 1000);
        System.out.println("PROVIDER STARTED (" + providerId + ")" );

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String EXCHANGE_REQUEST_NAME = "exchange_request";
        String EXCHANGE_RESPONSE_NAME = "exchange_response";
        channel.exchangeDeclare(EXCHANGE_REQUEST_NAME, BuiltinExchangeType.TOPIC, false);
        channel.exchangeDeclare(EXCHANGE_RESPONSE_NAME, BuiltinExchangeType.TOPIC, false);

        String REQUEST_QUEUE_NAME= "request_queue";
        channel.queueDeclare(REQUEST_QUEUE_NAME, false, false, false, null);

        bindTopics(REQUEST_QUEUE_NAME, EXCHANGE_REQUEST_NAME, channel);

        Consumer consumer = new DefaultConsumer(channel) {
            private int counter = 0;

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received: " + message);
                String[] splitKey = envelope.getRoutingKey().split("\\.");
                String sender = splitKey[0];
                if (!sender.equals("all")) { // nie admin wiec trzeba odpowiedziec
                    String key = sender + "." + providerId + "_" + (counter++);
                    String responseMessage = "Response to " + sender + " from " + providerId +  " for " + splitKey[1] + " request";
                    channel.basicPublish(EXCHANGE_RESPONSE_NAME, key,  null, responseMessage.getBytes());
                    System.out.println("Sent: " + responseMessage);
                } else if (splitKey[1].equals("providers")) {
                    System.out.println("received message to all providers: " + message);
                } else  {
                    System.out.println("received message to all: " + message);
                }
            }
        };

        System.out.println("PROVIDER READY");
        channel.basicConsume(REQUEST_QUEUE_NAME, true, consumer);
    }

    private static void bindTopics(String QUEUE_NAME, String EXCHANGE_REQUEST_NAME, Channel channel) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("give topic ('o' - oxygen, 'b' - backpack, 's' - shoes, 'd' - done");
        String sign = reader.readLine();

        channel.queueBind(QUEUE_NAME, EXCHANGE_REQUEST_NAME, "all");
        channel.queueBind(QUEUE_NAME, EXCHANGE_REQUEST_NAME, "all.providers");

        while (!sign.equals("d")) {
            channel.queueBind(QUEUE_NAME, EXCHANGE_REQUEST_NAME, "*." + getTopicKey(sign));
            System.out.println("give topic ('o' - oxygen, 'b' - backpack, 's' - shoes, 'd' - done");
            sign = reader.readLine();
        }
    }

    private static String getTopicKey(String sign) {
        return switch (sign) {
            case "o" -> "oxygen";
            case "s" -> "shoes";
            case "b" -> "backpack";
            default -> "";
        };
    }
}
