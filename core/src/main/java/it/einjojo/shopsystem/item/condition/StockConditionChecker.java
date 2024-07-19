package it.einjojo.shopsystem.item.condition;

import it.einjojo.shopsystem.item.ShopItem;
import org.bukkit.entity.Player;

public class StockConditionChecker implements ConditionChecker {

    @Override
    public boolean checkBuy(Player player, ShopItem item, int amount) {
        Integer stock = item.getStock();
        if (stock == null) return true;
        return stock >= amount;
    }

    @Override
    public boolean checkSell(Player player, ShopItem item, int amount) {
        return true;
    }

    @Override
    public String getBuyFailureText(Player player, ShopItem item, int amount) {
        return "<red>Leider ausverkauft!";
    }

    @Override
    public String getSellFailureText(Player player, ShopItem item, int amount) {
        return "";
    }
}
