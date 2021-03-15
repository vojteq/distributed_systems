package zad_dom;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Team {
    private static String teamId;
    private static Connection connection;
    private static Channel messageChannel;

    public static void main(String[] args) throws Exception {
        teamId = "team" + Math.abs(new Random().nextInt() % 1000);
        System.out.println("TEAM STARTED (" + teamId + ")");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        Channel requestChannel = connection.createChannel();

        requestChannel.exchangeDeclare(Utils.PRODUCT_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        new Thread(new MessageReader()).start();

        int counter = 0;
        while (true) {
            System.out.println("what product do u need ('o' - oxygen, 'b' - backpack, 's' - shoes)");
            String product = Utils.getProductName(reader.readLine());
            if (!product.equals("")) {
                AMQP.BasicProperties props = new AMQP.BasicProperties(
                        null, null, null, null, null,
                        null, null, null,
                        teamId + ":" + counter,
                        null, null, null, null, null);
                requestChannel.basicPublish(
                        Utils.PRODUCT_EXCHANGE_NAME,
                        product,
                        props,
                        ((teamId) + " need " + product).getBytes(StandardCharsets.UTF_8)
                );
                counter++;
            }
        }
    }

    private static class MessageReader implements Runnable {
        private Consumer consumer;

        @Override
        public void run() {
            try {
                init();
                System.out.println("MessageReader ready");
                messageChannel.basicConsume(teamId, true, consumer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void init() throws Exception {
            messageChannel = connection.createChannel();
            messageChannel.queueDeclare(teamId, false, true, true, null);
            messageChannel.queueBind(teamId, Utils.PRODUCT_EXCHANGE_NAME, "all");
            messageChannel.queueBind(teamId, Utils.PRODUCT_EXCHANGE_NAME, "all_teams");
            messageChannel.queueBind(teamId, Utils.PRODUCT_EXCHANGE_NAME, teamId);

            consumer = new DefaultConsumer(messageChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                    String message = new String(body, StandardCharsets.UTF_8);
                    if (envelope.getRoutingKey().equals(teamId)) {
                        System.out.println("Received response from provider: " + message);
                        System.out.println("OrderId: " + properties.getMessageId());
                    } else if (envelope.getRoutingKey().equals("all")) {
                        System.out.println("Received message to all: " + message);
                    } else if (envelope.getRoutingKey().equals("all_teams")) {
                        System.out.println("Received message to all teams: " + message);
                    }
                }
            };
        }
    }
}
