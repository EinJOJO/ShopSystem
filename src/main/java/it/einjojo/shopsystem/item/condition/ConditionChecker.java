package it.einjojo.shopsystem.item.condition;

import it.einjojo.shopsystem.item.ShopItem;
import org.bukkit.entity.Player;

public interface ConditionChecker {

    /**
     * Check if the player can buy the item
     *
     * @param player player who wants to buy the item
     * @param item   shop item that the player wants to buy
     * @return true if the player can buy the item, false otherwise
     */
    boolean checkBuy(Player player, ShopItem item);

    /**
     * Check if the player can sell the item
     *
     * @param player player who wants to sell the item
     * @param item   shop item that the player wants to sell
     * @return true if the player can sell the item, false otherwise
     */
    boolean checkSell(Player player, ShopItem item);

    /**
     * @param player player who wants to buy the item
     * @param item   shop item that the player wants to buy
     * @return Minimessage text that will be sent to the player when the purchase fails
     */
    String getBuyFailureText(Player player, ShopItem item);

    /**
     * @param player player who wants to sell the item
     * @param item   shop item that the player wants to sell
     * @return Minimessage text that will be sent to the player when the sale fails
     */
    String getSellFailureText(Player player, ShopItem item);


}
