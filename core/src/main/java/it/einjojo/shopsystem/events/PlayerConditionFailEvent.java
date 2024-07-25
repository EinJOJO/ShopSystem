package it.einjojo.shopsystem.events;

import it.einjojo.shopsystem.item.ShopItem;
import it.einjojo.shopsystem.item.condition.ConditionChecker;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * MainThread called - when a player fails the condition check.
 */
public class PlayerConditionFailEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Action action;
    private final ShopItem shopItem;
    private final ConditionChecker failedCondition;
    private final Component originalFailMessage;
    private Component failMessage;

    public PlayerConditionFailEvent(Player player, Action action, ShopItem item, ConditionChecker failedCondition, Component originalFailMessage) {
        super(player, false);
        this.action = action;
        this.failedCondition = failedCondition;
        this.originalFailMessage = originalFailMessage;
        this.shopItem = item;
    }

    public Action getAction() {
        return action;
    }

    public ShopItem getShopItem() {
        return shopItem;
    }

    public ConditionChecker getFailedCondition() {
        return failedCondition;
    }

    public Component getOriginalFailMessage() {
        return originalFailMessage;
    }

    public @Nullable Component getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(@Nullable Component failMessage) {
        this.failMessage = failMessage;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public enum Action {
        BUY,
        SELL
    }
}
