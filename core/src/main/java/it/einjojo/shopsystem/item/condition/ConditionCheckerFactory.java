package it.einjojo.shopsystem.item.condition;

import it.einjojo.shopsystem.economy.EconomyHandler;
import it.einjojo.shopsystem.item.ShopItem;

import java.util.LinkedList;
import java.util.List;

public class ConditionCheckerFactory {

    private final EconomyHandler economyHandler;

    public ConditionCheckerFactory(EconomyHandler economyHandler) {
        this.economyHandler = economyHandler;
    }

    public List<ConditionChecker> createConditionChecker(ShopItem item) {
        List<ConditionChecker> result = new LinkedList<>();
        if (item.isPurchasable()) {
            result.add(createMoneyChecker());
        }
        if (item.getStock() != null) {
            result.add(createStockChecker());
        }

        return result;
    }

    public StockConditionChecker createStockChecker() {
        return new StockConditionChecker();
    }

    public EnoughMoneyConditionChecker createMoneyChecker() {
        return new EnoughMoneyConditionChecker(economyHandler);
    }

}
