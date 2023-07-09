package ipb.pt.safeeat.constant;

public class OrderStatusConstant {
    public static final String REGISTERED = "REGISTERED";
    public static final String PREPARING = "PREPARING";
    public static final String TRANSPORTING = "TRANSPORTING";
    public static final String DELIVERED = "DELIVERED";
    public static final String CANCELED = "CANCELED";

    public static boolean isValid(String status) {
        return status.equals(REGISTERED)
                || status.equals(PREPARING)
                || status.equals(TRANSPORTING)
                || status.equals(DELIVERED)
                || status.equals(CANCELED);
    }
}
