package zad_dom_v2;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class Team {
    public static void main(String[] args) throws Exception {
        String teamId = "team" + Math.abs(new Random().nextInt() % 100);
        System.out.println("TEAM STARTED " + "(" + teamId + ")");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

//        String EXCHANGE_REQUEST_NAME = "exchange_request";
        String EXCHANGE_NAME = "exchange";

//        channel.exchangeDeclare(EXCHANGE_REQUEST_NAME, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        String QUEUE_NAME = "queue";
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "all");
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "all.teams");
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, teamId + ".*.response");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        Thread responseReader = new Thread(new ResponseReader(QUEUE_NAME, channel));
        responseReader.start();

        while (true) {
            System.out.println("what product do u need ('o' - oxygen, 'b' - backpack, 's' - shoes)");
            String product = getProduct(reader.readLine());
            if (!product.equals("")) {
                channel.basicPublish(EXCHANGE_NAME, teamId + "." + product, null, (teamId + " need " + product).getBytes());
            }
        }
    }

    private static String getProduct(String product) {
        return switch (product) {
            case "o" -> "oxygen";
            case "s" -> "shoes";
            case "b" -> "backpack";
            default -> "";
        };
    }

    private static class ResponseReader implements Runnable {

        private final String RESPONSE_QUEUE_NAME;
        private final Channel channel;

        public ResponseReader(String RESPONSE_QUEUE_NAME, Channel channel) {
            this.RESPONSE_QUEUE_NAME = RESPONSE_QUEUE_NAME;
            this.channel = channel;
        }

        @Override
        public void run() {
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    String[] splitKey = envelope.getRoutingKey().split("\\.");
                    if (!splitKey[0].equals("all")) {
                        System.out.println("received response from provider: " + message);
                    } else if (splitKey[1].equals("teams")) {
                        System.out.println("received message to all teams: " + message);
                    } else {
                        System.out.println("received message to all: " + message);
                    }
                }
            };

            try {
                channel.basicConsume(RESPONSE_QUEUE_NAME, true, consumer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
