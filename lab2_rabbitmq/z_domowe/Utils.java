package z_domowe;

public class Utils {


    public static final String TOPIC_EXCHANGE_NAME = "topic_exchange";
    public static final String BACKPACK_QUEUE_NAME = "backpack_queue";
    public static final String OXYGEN_QUEUE_NAME = "oxygen_queue";
    public static final String SHOES_QUEUE_NAME = "shoes_queue";


    public static String getMessage(String sign) {
        return switch (sign) {
            case "o" -> "oxygen";
            case "s" -> "shoes";
            case "b" -> "backpack";
            case "e" -> "end";
            default -> "";
        };
    }

    public static String getQueueName(String productType) {
        return switch (productType) {
            case "backpack" -> BACKPACK_QUEUE_NAME;
            case "oxygen" -> OXYGEN_QUEUE_NAME;
            case "shoes" -> SHOES_QUEUE_NAME;
            default -> "";
        };
    }
}
