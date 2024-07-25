package it.einjojo.shopsystem.item;

/**
 * The reason will be displayed to the user when the trade fails
 */
public class ItemTradeException extends Exception {
    private final Reason reason;


    public ItemTradeException(Reason reason) {
        super(reason.name());
        this.reason = reason;
    }

    public ItemTradeException(Throwable cause) {
        super(cause);
        this.reason = Reason.EXCEPTION;
    }


    public Reason getReason() {
        return reason;

    }


    public enum Reason {
        ARTICLE_NOT_SELLABLE,
        ARTICLE_NOT_BUYABLE,
        ITEM_REMOVAL_FAILED,
        EXCEPTION,
        UNKNOWN,
        INSUFFICIENT_FUNDS

    }

}
