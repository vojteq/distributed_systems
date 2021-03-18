package z_domowe;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Provider {

    private static Connection connection;
    private static final String providerId = "provider" + Math.abs(new Random().nextInt() % 1000);
    private static final ReentrantLock lock = new ReentrantLock();
    private static int counter = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("PROVIDER STARTED (" + providerId + ")");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();

        List<Thread> listeners = initListeners();
        new Thread(new AdminMessageListener()).start();

        for (Thread t : listeners) {
            t.start();
        }
    }

    private static List<Thread> initListeners() throws Exception {
        List<Thread> listeners = new ArrayList<>();
        List<String> subscribed = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("enter product ('o' - oxygen, 'b' - backpack, 's' - shoes, 'd' - done");
        String sign = reader.readLine();

        while (!sign.equals("d")) {
            String product = Utils.getMessage(sign);
            if (!product.equals("") && !subscribed.contains(product)) {
                subscribed.add(product);
                listeners.add(new Thread(new ProductRequestListener(connection, providerId, product, Utils.getQueueName(product))));
            }
            System.out.println("enter product ('o' - oxygen, 'b' - backpack, 's' - shoes, 'd' - done");
            sign = reader.readLine();
        }

        return listeners;
    }

    public static int getCounterAndIncrement() {
        lock.lock();
        try {
            return counter++;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return -1;
    }

    private static class AdminMessageListener implements Runnable {

        private Channel channel;
        private Consumer consumer;

        @Override
        public void run() {
            try {
                init();
                channel.basicConsume(providerId, true, consumer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void init() throws IOException {
            channel = connection.createChannel();
            channel.queueDeclare(providerId, false, false, true, null);
            channel.queueBind(providerId, Utils.TOPIC_EXCHANGE_NAME, "all");
            channel.queueBind(providerId, Utils.TOPIC_EXCHANGE_NAME, "all.providers");

            consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                    String message = new String(body, StandardCharsets.UTF_8);
                    if (envelope.getRoutingKey().equals("all")) {
                        System.out.println("Received message to all: " + message);
                    } else {
                        System.out.println("Received message to all providers" + ": " + message);
                    }
                }
            };
        }
    }
}
