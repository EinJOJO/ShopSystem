package it.einjojo.shopsystem.item.condition;

import it.einjojo.shopsystem.economy.EconomyHandler;
import it.einjojo.shopsystem.item.ShopItem;
import org.bukkit.entity.Player;

public class EnoughMoneyConditionChecker implements ConditionChecker {
    private final EconomyHandler economyHandler;

    public EnoughMoneyConditionChecker(EconomyHandler economyHandler) {
        this.economyHandler = economyHandler;
    }

    @Override
    public boolean checkBuy(Player player, ShopItem item) {
        return false;
    }

    @Override
    public boolean checkSell(Player player, ShopItem item) {
        return false;
    }

    @Override
    public String getBuyFailureText(Player player, ShopItem item) {
        return "Du hast nicht genügend Geld";
    }

    @Override
    public String getSellFailureText(Player player, ShopItem item) {
        return "";
    }
}
