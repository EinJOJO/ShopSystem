package it.einjojo.shopsystem.gui;

import it.einjojo.shopsystem.ShopSystemPlugin;
import it.einjojo.shopsystem.item.ShopItem;
import it.einjojo.shopsystem.item.ItemTradeException;
import mc.obliviate.inventory.Icon;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface ShopItemIconFactory {
    ShopItemIconFactory SELL_ONLY = new SellOnly();
    ShopItemIconFactory BUY_ONLY = new BuyOnly();
    ShopItemIconFactory DEFAULT = new Default();

    /**
     * Create an icon for the shop item
     *
     * @param shopItem the shop item
     * @return the icon for the shop item or null if no icon was created.
     */
    @Nullable
    Icon createIcon(ShopItem shopItem, ShopSystemPlugin plugin);

    class SellOnly implements ShopItemIconFactory {

        @Override
        public @Nullable Icon createIcon(ShopItem shopItem, ShopSystemPlugin plugin) {
            return new Icon(shopItem.getDisplayItemBase().clone())
                    .setLore("", "Verkaufen für " + shopItem.getSellPrice() + " Coins")
                    .onClick((clickEvent) -> {
                        Player player = (Player) clickEvent.getWhoClicked();
                        try {
                            playActionSound(
                                    shopItem.sell(player, plugin, 1),
                                    player
                            );
                        } catch (ItemTradeException e) {
                            playActionSound(false, player);
                            player.sendMessage(plugin.getMiniMessage().deserialize("<prefix><red>Kauf konnte nicht durchgeührt werden: <reason>",
                                    Placeholder.unparsed("reason", e.getReason().name())));
                            ShopItemIconFactory.handleTradeException(e);
                        }
                    });
        }
    }

    private static void handleTradeException(ItemTradeException exception) {
        if (exception.getReason().equals(ItemTradeException.Reason.EXCEPTION)) {
            throw new RuntimeException(exception);
        }
    }



    class BuyOnly implements ShopItemIconFactory {

        @Override
        public @Nullable Icon createIcon(ShopItem shopItem, ShopSystemPlugin plugin) {
            return new Icon(shopItem.getDisplayItemBase().clone())
                    .setLore("", "Kaufen für " + shopItem.getBuyPrice() + " Coins")
                    .onClick((clickEvent) -> {
                        Player player = (Player) clickEvent.getWhoClicked();
                        try {
                            playActionSound(
                                    shopItem.buy(player, plugin, 1),
                                    player
                            );
                        } catch (ItemTradeException e) {
                            playActionSound(false, player);
                            player.sendMessage(plugin.getMiniMessage().deserialize("<prefix><red>Verkauf konnte nicht durchgeührt werden: <reason>",
                                    Placeholder.unparsed("reason", e.getReason().name())));
                            ShopItemIconFactory.handleTradeException(e);
                        }

                    });
        }
    }

    static void playActionSound(boolean success, Player player) {
        player.playSound(player.getLocation(), success ? Sound.ENTITY_PLAYER_LEVELUP : Sound.ENTITY_VILLAGER_NO, 1, success ? 1 : 0.5f);
    }

    class Default implements ShopItemIconFactory {
        @Override
        public @Nullable Icon createIcon(ShopItem shopItem, ShopSystemPlugin plugin) {
            return new Icon(shopItem.getDisplayItem().clone()).onClick((clickEvent) -> {
                try {
                    shopItem.buy((Player) clickEvent.getWhoClicked(), plugin, 1);
                } catch (ItemTradeException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

}
