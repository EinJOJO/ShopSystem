package it.einjojo.shopsystem.shop;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface Shop {

    String getId();

    Component getShopGuiTitle();

    /**
     * Opens the shop for the given player
     *
     * @param player the player to open the shop for
     * @deprecated use {@link #openBuy(Player)} or {@link #openSell(Player)} instead (because Felix changes his mind after I planed the plugin)
     */
    @Deprecated
    default void open(Player player) {
        openBuy(player);
    }


    void openBuy(Player player);

    void openSell(Player player);

    @Nullable
    ShopChangeObserver getObserver();

    void setObserver(@Nullable ShopChangeObserver observer);

    default void callChangeObserver() {
        ShopChangeObserver observer = getObserver();
        if (observer != null) {
            observer.onShopChange(this);
        }
    }

}
