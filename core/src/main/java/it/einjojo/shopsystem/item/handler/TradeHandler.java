package it.einjojo.shopsystem.item.handler;

import it.einjojo.shopsystem.item.ItemTradeException;
import org.bukkit.entity.Player;

/**
 *
 */
public interface TradeHandler {

    /**
     * Executed when the player has purchased an item successfully.
     *
     * @param player The player who purchased the item
     * @param amount the quantity of the item purchased
     * @throws ItemTradeException if the item could not be given to the player
     */
    void giveItem(Player player, int amount) throws ItemTradeException;

    /**
     * Executed when the player wants to sell an item.
     *
     * @param player The player who sold the item
     * @param amount the quantity of the item sold
     * @throws ItemTradeException if the item could not be taken from the player
     */
    void removeItem(Player player, int amount) throws ItemTradeException;

}
