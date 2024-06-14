package it.einjojo.shopsystem.item;

import it.einjojo.shopsystem.item.condition.ConditionChecker;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ShopItemBuilder {
    private static final ItemStack NO_DISPLAY_ITEM = new ItemStack(Material.PAPER);
    private final List<ConditionChecker> conditionCheckerList = new LinkedList<>();
    private ItemTradeHandler itemTradeHandler;
    private ItemStack displayItem = NO_DISPLAY_ITEM;
    private Integer buyPrice;
    private Integer sellPrice;
    private Integer stock;
    private ShopItemObserver observer;

    public ShopItemBuilder item(ItemTradeHandler itemTradeHandler) {
        this.itemTradeHandler = itemTradeHandler;
        return this;
    }


    public ShopItemBuilder observer(ShopItemObserver observer) {
        this.observer = observer;
        return this;
    }

    public ShopItemBuilder buyPrice(Integer buyPrice) {
        this.buyPrice = buyPrice;
        return this;
    }

    public ShopItemBuilder sellPrice(Integer sellPrice) {
        this.sellPrice = sellPrice;
        return this;
    }

    public ShopItemBuilder displayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
        return this;
    }

    public ShopItemBuilder addCondition(ConditionChecker conditionChecker) {
        conditionCheckerList.add(conditionChecker);
        return this;
    }

    public ShopItemBuilder addConditions(Collection<ConditionChecker> conditionCheckers) {
        conditionCheckerList.addAll(conditionCheckers);
        return this;
    }


    public ShopItemBuilder stock(Integer stock) {
        this.stock = stock;
        return this;
    }

    public ShopItem build() {
        var shopItem = new ShopItem(itemTradeHandler, displayItem, buyPrice, sellPrice, stock, conditionCheckerList);
        shopItem.setObserver(observer);
        return shopItem;
    }


}
