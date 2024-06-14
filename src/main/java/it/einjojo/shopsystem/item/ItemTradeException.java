package it.einjojo.shopsystem.item;

/**
 * The reason will be displayed to the user when the trade fails
 */
public class ItemTradeException extends Exception {
    private final String reason;

    public ItemTradeException(String reason) {
        this.reason = reason;
    }

    public ItemTradeException(Throwable cause) {
        super(cause);
        this.reason = cause.getMessage();
    }


    public String getReason() {
        return reason;
    }


}
