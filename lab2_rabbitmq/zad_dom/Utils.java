package zad_dom;

public class Utils {


    public static final String PRODUCT_EXCHANGE_NAME = "direct_exchange";
    public static final String BACKPACK_QUEUE_NAME = "backpack_queue";
    public static final String OXYGEN_QUEUE_NAME = "oxygen_queue";
    public static final String SHOES_QUEUE_NAME = "shoes_queue";

    public static final String ADMIN_EXCHANGE_NAME = "admin_exchange";


    public static String getProductName(String productSign) {
        return switch (productSign) {
            case "o" -> "oxygen";
            case "s" -> "shoes";
            case "b" -> "backpack";
            default -> "";
        };
    }

    public static String getQueueName(String productType) {
        return switch (productType) {
            case "backpack" -> BACKPACK_QUEUE_NAME;
            case "oxygen" -> OXYGEN_QUEUE_NAME;
            case "SHOES" -> SHOES_QUEUE_NAME;
            default -> "";
        };
    }
}
