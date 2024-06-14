package it.einjojo.shopsystem.item;

import it.einjojo.shopsystem.item.condition.ConditionChecker;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ShopItem {
    @NotNull
    private final ItemTradeHandler itemTradeHandler;
    @NotNull
    private final List<ConditionChecker> conditionCheckerList;
    @NotNull
    private ItemStack displayItemBase;
    @Nullable
    private Integer buyPrice;
    @Nullable
    private Integer sellPrice;
    @Nullable
    private transient ShopItemObserver observer;

    @Nullable
    private Integer stock;

    public ShopItem(@NotNull ItemTradeHandler itemTradeHandler, @NotNull ItemStack displayItemBase, @Nullable Integer buyPrice, @Nullable Integer sellPrice, @Nullable Integer stock, @NotNull List<ConditionChecker> conditionCheckerList) {
        this.itemTradeHandler = itemTradeHandler;
        this.displayItemBase = displayItemBase;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.stock = stock;
        this.conditionCheckerList = conditionCheckerList;
    }

    public static ShopItemBuilder builder() {
        return new ShopItemBuilder();
    }



    /**
     * Immutable list of conditions
     *
     * @return List of conditions
     */
    public List<ConditionChecker> getConditionList() {
        return Collections.unmodifiableList(conditionCheckerList);
    }

    /**
     * @param player Player to check
     * @return failed condition or null if all conditions are met
     */
    protected @Nullable ConditionChecker checkBuyCondition(Player player) {
        for (ConditionChecker conditionChecker : conditionCheckerList) {
            if (!conditionChecker.checkBuy(player, this)) {
                return conditionChecker;
            }
        }
        return null;
    }

    /**
     * @param player Player to check
     * @return failed condition or null if all conditions are met
     */
    protected @Nullable ConditionChecker checkSellCondition(Player player) {
        for (ConditionChecker conditionChecker : conditionCheckerList) {
            if (!conditionChecker.checkSell(player, this)) {
                return conditionChecker;
            }
        }
        return null;
    }

    public void setObserver(@Nullable ShopItemObserver observer) {
        this.observer = observer;
    }

    public void callChangeObserver() {
        if (observer != null) {
            observer.onItemChange(this);
        }
    }

    public ItemTradeHandler getItem() {
        return itemTradeHandler;
    }

    public ItemStack getDisplayItem() {
        return displayItemBase;
    }

    public void setDisplayItemBase(ItemStack displayItemBase) {
        this.displayItemBase = displayItemBase;
        callChangeObserver();

    }

    public boolean isPurchasable() {
        return buyPrice != null;
    }

    public boolean isSellable() {
        return sellPrice != null;
    }

    public @Nullable Integer getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(@Nullable Integer buyPrice) {
        this.buyPrice = buyPrice;
        callChangeObserver();
    }

    public @Nullable Integer getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(@Nullable Integer sellPrice) {
        this.sellPrice = sellPrice;
        callChangeObserver();
    }

    public @Nullable Integer getStock() {
        return stock;
    }

    public void setStock(@Nullable Integer stock) {
        this.stock = stock;
        callChangeObserver();
    }

    public void addCondition(ConditionChecker conditionChecker) {
        if (conditionChecker == null) return;
        conditionCheckerList.add(conditionChecker);
        callChangeObserver();
    }

    public void removeCondition(ConditionChecker conditionChecker) {
        if (conditionChecker == null) return;
        conditionCheckerList.remove(conditionChecker);
        callChangeObserver();
    }
}
