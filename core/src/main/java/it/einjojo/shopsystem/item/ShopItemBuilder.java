package it.einjojo.shopsystem.item;

import com.google.common.base.Preconditions;
import it.einjojo.shopsystem.item.condition.ConditionChecker;
import it.einjojo.shopsystem.item.handler.ItemStackTradeHandler;
import it.einjojo.shopsystem.item.handler.TradeHandler;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Builder for {@link ShopItem}
 */
public class ShopItemBuilder {
    private static final ItemStack NO_DISPLAY_ITEM = new ItemStack(Material.PAPER);
    private final List<ConditionChecker> conditionCheckerList = new LinkedList<>();
    private TradeHandler tradeHandler;
    private ItemStack displayItem = NO_DISPLAY_ITEM;
    private Integer buyPrice;
    private Integer sellPrice;
    private Integer stock;
    private ShopItemObserver observer;

    public ShopItemBuilder() {
    }

    public ShopItemBuilder(ShopItem shopItem) {
        this.tradeHandler = shopItem.getItemTradeHandler();
        this.displayItem = shopItem.getDisplayItem();
        this.buyPrice = shopItem.getBuyPrice();
        this.sellPrice = shopItem.getSellPrice();
        this.stock = shopItem.getStock();
        this.observer = shopItem.getObserver();
        this.conditionCheckerList.addAll(shopItem.getConditionList());
    }

    public ShopItemBuilder itemTradeHandler(TradeHandler tradeHandler) {
        this.tradeHandler = tradeHandler;
        return this;
    }

    /**
     * Sets item trade handler and display icon to the item
     *
     * @return this builder
     * @params itemsStack should not be air or null.
     */
    public ShopItemBuilder withItemStack(@NotNull ItemStack itemStack) {
        Preconditions.checkNotNull(itemStack);
        if (itemStack.getType().isAir()) {
            throw new IllegalArgumentException("ItemStack must not be air");
        }

        itemTradeHandler(new ItemStackTradeHandler(itemStack));
        displayItem(itemStack);
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
        var shopItem = new ShopItem(tradeHandler, displayItem, buyPrice, sellPrice, stock, conditionCheckerList);
        shopItem.setObserver(observer);
        return shopItem;
    }


}
