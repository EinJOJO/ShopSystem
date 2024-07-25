package it.einjojo.shopsystem.events;

import it.einjojo.shopsystem.item.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerShopBuyEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final ShopItem shopItem;
    private int amount;
    private boolean cancelled;

    public PlayerShopBuyEvent(@NotNull Player who, ShopItem shopItem, int amount) {
        super(who);
        this.shopItem = shopItem;
    }

public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public ShopItem getShopItem() {
        return shopItem;
    }


    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
