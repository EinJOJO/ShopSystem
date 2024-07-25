package it.einjojo.shopsystem.item;

import it.einjojo.shopsystem.ShopSystemPlugin;
import it.einjojo.shopsystem.events.PlayerConditionFailEvent;
import it.einjojo.shopsystem.events.PlayerShopSellEvent;
import it.einjojo.shopsystem.item.condition.ConditionChecker;
import it.einjojo.shopsystem.item.handler.TradeHandler;
import it.einjojo.shopsystem.util.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * Item that can be bought and sold in a shop with conditions.
 * Conditions are checked before the item can be bought or sold.
 */
public class ShopItem {
    @NotNull
    private final TradeHandler tradeHandler;
    @NotNull
    private final List<ConditionChecker> conditionCheckerList = new LinkedList<>();
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

    public ShopItem(@NotNull TradeHandler tradeHandler, @NotNull ItemStack displayItemBase, @Nullable Integer buyPrice, @Nullable Integer sellPrice, @Nullable Integer stock, @NotNull Collection<ConditionChecker> conditionChecks) {
        this.tradeHandler = tradeHandler;
        this.displayItemBase = displayItemBase;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.stock = stock;
        this.conditionCheckerList.addAll(conditionChecks);
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
    protected @Nullable ConditionChecker checkBuyCondition(Player player, int amount) {
        for (ConditionChecker conditionChecker : conditionCheckerList) {
            if (!conditionChecker.checkBuy(player, this, amount)) {
                return conditionChecker;
            }
        }
        return null;
    }

    /**
     * @param player Player to check
     * @return failed condition or null if all conditions are met
     */
    protected @Nullable ConditionChecker checkSellCondition(Player player, int amount) {
        for (ConditionChecker conditionChecker : conditionCheckerList) {
            if (!conditionChecker.checkSell(player, this, amount)) {
                return conditionChecker;
            }
        }
        return null;
    }

    public void setObserver(@Nullable ShopItemObserver observer) {
        this.observer = observer;
    }

    public TradeHandler getItem() {
        return tradeHandler;
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

    /**
     * Buy the item
     *
     * @param buyer  Player who wants to buy the item
     * @param plugin ShopSystemPlugin
     * @param amount Amount of items to buy
     * @return true if the item was bought successfully and false if the item could not be bought. e.g failed condition checks or cancelled event
     * @throws ItemTradeException    if the item could not be bought because of weird reasons ({@link ItemTradeException.Reason})
     * @throws IllegalStateException if the method is not called from the main thread
     */
    public boolean buy(Player buyer, ShopSystemPlugin plugin, int amount) throws ItemTradeException {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("buy() must be called from the main thread");
        }
        if (buyPrice == null) {
            throw new ItemTradeException(ItemTradeException.Reason.ARTICLE_NOT_BUYABLE);
        }
        var buyEvent = new PlayerShopSellEvent(buyer, this, amount);
        if (!buyEvent.callEvent()) {
            return false;
        }
        amount = buyEvent.getAmount();
        final int buyPrice = this.buyPrice * amount;
        ConditionChecker failed = checkBuyCondition(buyer, amount);
        if (failed != null) {
            Component component = plugin.getMiniMessage().deserialize(failed.getBuyFailureText(buyer, this, amount), getTagResolvers());
            var event = new PlayerConditionFailEvent(buyer, PlayerConditionFailEvent.Action.BUY, this, failed, component);
            event.callEvent();
            Component failMessage = event.getFailMessage();
            if (failMessage != null) {
                buyer.sendMessage(failMessage);
            }
            return false;
        }
        if (!plugin.getEconomyHandler().has(buyer.getUniqueId(), buyPrice)) {
            throw new ItemTradeException(ItemTradeException.Reason.INSUFFICIENT_FUNDS);
        }
        plugin.getEconomyHandler().remove(buyer.getUniqueId(), buyPrice);
        try {
            getItemTradeHandler().giveItem(buyer, amount);
        } catch (ItemTradeException e) {
            plugin.getEconomyHandler().add(buyer.getUniqueId(), buyPrice);
            throw e;
        }
        if (stock != null) {
            setStock(stock - amount);
        }
        return true;
    }

    /**
     * Sell the item
     *
     * @param player Player who wants to sell the item
     * @param plugin ShopSystemPlugin
     * @param amount Amount of items to sell
     * @return true if the item was sold successfully and false if the item could not be sold. e.g failed condition checks or cancelled event
     * @throws ItemTradeException    if the item could not be sold because of weird reasons {@link ItemTradeException.Reason}
     * @throws IllegalStateException if the method is not called from the main thread
     */
    public boolean sell(Player player, ShopSystemPlugin plugin, int amount) throws ItemTradeException {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("sell() must be called from the main thread");
        }
        if (sellPrice == null) {
            throw new ItemTradeException(ItemTradeException.Reason.ARTICLE_NOT_SELLABLE);
        }
        var sellEvent = new PlayerShopSellEvent(player, this, amount);
        if (!sellEvent.callEvent()) {
            return false;
        }
        amount = sellEvent.getAmount();
        ConditionChecker failed = checkSellCondition(player, amount);
        if (failed != null) {
            Component component = plugin.getMiniMessage().deserialize(failed.getSellFailureText(player, this, amount), getTagResolvers());
            var event = new PlayerConditionFailEvent(player, PlayerConditionFailEvent.Action.SELL, this, failed, component);
            event.callEvent();
            Component failMessage = event.getFailMessage();
            if (failMessage != null) {
                player.sendMessage(failMessage);
            }
            return false;
        }
        getItemTradeHandler().removeItem(player, amount);
        plugin.getEconomyHandler().add(player.getUniqueId(), sellPrice * amount);
        if (stock != null) {
            setStock(stock + amount);
        }
        return true;
    }


    public @Nullable Integer getStock() {
        return stock;
    }

    /**
     * @param stock Stock of the item. Null if the item has no stock limit
     * @throws IllegalArgumentException if stock is negative
     */
    public void setStock(@Nullable Integer stock) {
        if (stock != null && stock < 0) {
            throw new IllegalArgumentException("Stock must be positive or null");
        }
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

    public @NotNull TradeHandler getItemTradeHandler() {
        return tradeHandler;
    }


    /**
     * A clone of the base item
     *
     * @return ItemStack
     */
    public @NotNull ItemStack getDisplayItemBase() {
        return displayItemBase.clone();
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
        return Objects.equals(tradeHandler, shopItem.tradeHandler) && Objects.equals(conditionCheckerList, shopItem.conditionCheckerList) && Objects.equals(displayItemBase, shopItem.displayItemBase) && Objects.equals(displayItemCached, shopItem.displayItemCached) && Objects.equals(buyPrice, shopItem.buyPrice) && Objects.equals(sellPrice, shopItem.sellPrice) && Objects.equals(observer, shopItem.observer) && Objects.equals(stock, shopItem.stock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradeHandler, conditionCheckerList, displayItemBase, displayItemCached, buyPrice, sellPrice, observer, stock);
    }

    @Override
    public String toString() {
        return "ShopItem{" +
                "itemTradeHandler=" + tradeHandler.getClass().getSimpleName() +
                ", conditions=" + conditionCheckerList.size() +
                ", displayItemBase=" + displayItemBase.getType().name() +
                ", buyPrice=" + buyPrice +
                ", sellPrice=" + sellPrice +
                ", observed=" + ((observer == null) ? "no" : "yes") +
                ", stock=" + stock +
                '}';
    }
}
