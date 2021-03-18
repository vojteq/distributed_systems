package zad_dom;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ProductRequestListener implements Runnable {

    private final Connection connection;
    private final String providerId;
    private final String productType;
    private final String queueName;

    public ProductRequestListener(Connection connection, String providerId, String productType, String queueName) {
        this.connection = connection;
        this.providerId = providerId;
        this.productType = productType;
        this.queueName = queueName;
    }

    @Override
    public void run() {
        try {
            Channel channel = createChannel();

            Consumer consumer = new DefaultConsumer(channel) {

                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, StandardCharsets.UTF_8);
                    System.out.println("Received: " + message);
                    String teamId = message.split(" ")[0];
                    String requestId = properties.getMessageId();
                    String responseId = providerId + ":" + Provider.getCounterAndIncrement();
                    String responseMessage = "Response to " + teamId + " from " + providerId + " for " + envelope.getRoutingKey() + " request" + "(" + requestId + ")";


                    AMQP.BasicProperties props = new AMQP.BasicProperties(
                            null, null, null, null, null,
                            null, null, null,
                            requestId + " " + responseId,
                            null, null, null, null, null);
                    channel.basicPublish(
                            Utils.TOPIC_EXCHANGE_NAME,
                            "teams." + teamId,
                            props,
                            responseMessage.getBytes());
                    System.out.println("Sent: " + responseMessage);
                }
            };

            System.out.println("REQUEST LISTENER READY (" + productType + ")");
            channel.basicConsume(queueName, true, consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Channel createChannel() throws IOException {
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(Utils.TOPIC_EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        channel.queueDeclare(queueName, false, false, true, null);
        channel.queueBind(queueName, Utils.TOPIC_EXCHANGE_NAME, "providers." + productType);
        return channel;
    }
}