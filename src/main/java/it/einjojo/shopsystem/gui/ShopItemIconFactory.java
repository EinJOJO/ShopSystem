package it.einjojo.shopsystem.gui;

import it.einjojo.shopsystem.item.ShopItem;
import mc.obliviate.inventory.Icon;
import org.jetbrains.annotations.Nullable;

public interface ShopItemIconFactory {
    ShopItemIconFactory SELL_ONLY = new SellOnly();
    ShopItemIconFactory BUY_ONLY = new BuyOnly();

    /**
     * Create an icon for the shop item
     *
     * @param shopItem the shop item
     * @return the icon for the shop item or null if no icon was created.
     */
    @Nullable
    Icon createIcon(ShopItem shopItem);

    class SellOnly implements ShopItemIconFactory {

        @Override
        public @Nullable Icon createIcon(ShopItem shopItem) {
            return new Icon(shopItem.getDisplayItem()).setLore("", "Verkaufen für " + shopItem.getSellPrice() + " Coins");
        }
    }

    class BuyOnly implements ShopItemIconFactory {

        @Override
        public @Nullable Icon createIcon(ShopItem shopItem) {
            return new Icon(shopItem.getDisplayItem());
        }
    }

}
