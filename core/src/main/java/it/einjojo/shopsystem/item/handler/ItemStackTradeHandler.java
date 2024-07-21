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
            player.getInventory().addItem(itemStack.asQuantity(amount));
        } catch (Exception ex) {
            throw new ItemTradeException(ex);
        }
    }

    @Override
    public void removeItem(Player player, int amount) throws ItemTradeException {
        try {
            var notRemovedMap = player.getInventory().removeItem(itemStack.asQuantity(amount));
            if (!notRemovedMap.isEmpty()) { // Conditions should be checked before the trade is executed but just to be safe.
                var notRemoved = notRemovedMap.get(0); // Since only one argument is parsed, the map will only contain one entry
                int refundAmount = (amount - notRemoved.getAmount());
                player.getInventory().addItem(itemStack.asQuantity(refundAmount));
                throw new ItemTradeException(ItemTradeException.Reason.ITEM_REMOVAL_FAILED);
            }
            ;
        } catch (Exception ex) {
            throw new ItemTradeException(ex);
        }
    }
}
