package it.einjojo.shopsystem.item.condition;

import it.einjojo.shopsystem.economy.EconomyHandler;
import it.einjojo.shopsystem.item.ShopItem;
import org.bukkit.entity.Player;

/**
 * Checks if the player has enough money to buy the item
 */
public class EnoughMoneyConditionChecker implements ConditionChecker {
    private final EconomyHandler economyHandler;

    public EnoughMoneyConditionChecker(EconomyHandler economyHandler) {
        this.economyHandler = economyHandler;
    }

    @Override
    public boolean checkBuy(Player player, ShopItem item, int amount) {
        Integer buyPrice = item.getBuyPrice();
        if (buyPrice == null) return false;
        return economyHandler.has(player.getUniqueId(), buyPrice * amount);
    }

    @Override
    public boolean checkSell(Player player, ShopItem item, int amount) {
        return true;
    }

    @Override
    public String getBuyFailureText(Player player, ShopItem item, int amount) {
        return "<red>Du hast nicht genügend Geld.";
    }

    @Override
    public String getSellFailureText(Player player, ShopItem item, int amount) {
        return "";
    }
}
