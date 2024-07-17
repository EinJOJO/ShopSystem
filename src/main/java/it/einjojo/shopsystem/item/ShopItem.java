package it.einjojo.shopsystem.item;

import it.einjojo.shopsystem.item.condition.ConditionChecker;
import it.einjojo.shopsystem.item.handler.ItemTradeHandler;
import it.einjojo.shopsystem.util.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Item that can be bought and sold in a shop with conditions.
 * Conditions are checked before the item can be bought or sold.
 */
public class ShopItem {
    @NotNull
    private final ItemTradeHandler itemTradeHandler;
    @NotNull
    private final List<ConditionChecker> conditionCheckerList;
    @NotNull
    private ItemStack displayItemBase;
    private transient ItemStack displayItemCached;
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
        updateDisplayItem();
    }

    public void updateDisplayItem() {
        displayItemCached = displayItemBase.clone();
        displayItemCached.setAmount(1);
        displayItemCached.editMeta(meta -> {
            List<Component> lore = new LinkedList<>();
            lore.add(Component.empty());
            TagResolver[] tagResolvers = getTagResolvers();
            if (isPurchasable()) {
                lore.add(Messages.get().deserialize("<gray>Kaufpreis: <yellow><buy-price>", tagResolvers));
            }
            if (isSellable()) {
                lore.add(Messages.get().deserialize("<gray>Verkaufspreis: <yellow><sell-price>", tagResolvers));
            }
            if (stock != null) {
                lore.add(Messages.get().deserialize("<gray>Lagerbestand: <stock>", tagResolvers));
            }
            lore.add(Component.empty());
            meta.lore(lore);
        });
    }

    public TagResolver[] getTagResolvers() {
        return new TagResolver[]{
                Placeholder.parsed("buy-price", buyPrice != null ? buyPrice.toString() : "<red>N/A</red>"),
                Placeholder.parsed("sell-price", sellPrice != null ? sellPrice.toString() : "<red>N/A</red>"),
                Placeholder.parsed("stock", stock != null ? stock.toString() : "<red>N/A</red>")
        };
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

    public ItemTradeHandler getItem() {
        return itemTradeHandler;
    }

    public ItemStack getDisplayItem() {
        return displayItemCached;
    }

    public void setDisplayItemBase(ItemStack displayItemBase) {
        this.displayItemBase = displayItemBase;
        updateDisplayItem();
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
        updateDisplayItem();
        callChangeObserver();
        withObserver(observer -> observer.onBuyPriceChange(this));
    }

    public @Nullable Integer getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(@Nullable Integer sellPrice) {
        this.sellPrice = sellPrice;
        updateDisplayItem();
        callChangeObserver();
        withObserver(observer -> observer.onSellPriceChange(this));
    }

    public @Nullable Integer getStock() {
        return stock;
    }

    public void setStock(@Nullable Integer stock) {
        this.stock = stock;
        updateDisplayItem();
        callChangeObserver();
        withObserver(observer -> observer.onStockChange(this));
    }

    public void addCondition(ConditionChecker conditionChecker) {
        if (conditionChecker == null) return;
        conditionCheckerList.add(conditionChecker);
        callChangeObserver();
        withObserver(observer -> observer.onConditionChange(this));
    }

    public void removeCondition(ConditionChecker conditionChecker) {
        if (conditionChecker == null) return;
        conditionCheckerList.remove(conditionChecker);
        callChangeObserver();
        withObserver(observer -> observer.onConditionChange(this));
    }

    public @NotNull ItemTradeHandler getItemTradeHandler() {
        return itemTradeHandler;
    }


    public @NotNull ItemStack getDisplayItemBase() {
        return displayItemBase;
    }

    public @Nullable ShopItemObserver getObserver() {
        return observer;
    }

    private void callChangeObserver() {
        withObserver(observer -> observer.onChange(this));
    }

    private void withObserver(Consumer<@NotNull ShopItemObserver> consumer) {
        if (observer != null) {
            consumer.accept(observer);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopItem shopItem = (ShopItem) o;
        return Objects.equals(itemTradeHandler, shopItem.itemTradeHandler) && Objects.equals(conditionCheckerList, shopItem.conditionCheckerList) && Objects.equals(displayItemBase, shopItem.displayItemBase) && Objects.equals(displayItemCached, shopItem.displayItemCached) && Objects.equals(buyPrice, shopItem.buyPrice) && Objects.equals(sellPrice, shopItem.sellPrice) && Objects.equals(observer, shopItem.observer) && Objects.equals(stock, shopItem.stock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemTradeHandler, conditionCheckerList, displayItemBase, displayItemCached, buyPrice, sellPrice, observer, stock);
    }

    @Override
    public String toString() {
        return "ShopItem{" +
                "itemTradeHandler=" + itemTradeHandler.getClass().getSimpleName() +
                ", conditions=" + conditionCheckerList.size() +
                ", displayItemBase=" + displayItemBase.getType().name() +
                ", buyPrice=" + buyPrice +
                ", sellPrice=" + sellPrice +
                ", observed=" + ((observer == null) ? "no" : "yes") +
                ", stock=" + stock +
                '}';
    }
}
