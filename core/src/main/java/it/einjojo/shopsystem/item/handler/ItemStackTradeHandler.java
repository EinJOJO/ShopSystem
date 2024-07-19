package it.einjojo.shopsystem.item.handler;

import it.einjojo.shopsystem.item.ItemTradeException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemStackTradeHandler implements TradeHandler {
    private final ItemStack itemStack;

    public ItemStackTradeHandler(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public void giveItem(Player player, int amount) throws ItemTradeException {
        try {
            player.getInventory().addItem(itemStack);
        } catch (Exception ex) {
            throw new ItemTradeException(ex);
        }
    }

    @Override
    public void removeItem(Player player, int amount) throws ItemTradeException {
        try {
            player.getInventory().removeItem(itemStack);
        } catch (Exception ex) {
            throw new ItemTradeException(ex);
        }
    }
}
