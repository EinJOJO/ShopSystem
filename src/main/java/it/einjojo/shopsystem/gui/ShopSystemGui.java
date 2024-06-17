package it.einjojo.shopsystem.gui;

import mc.obliviate.inventory.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

public class ShopSystemGui extends Gui {
    public ShopSystemGui(@NotNull Player player, @NotNull String id, String title, int rows) {
        super(player, id, title, rows);
    }

    public ShopSystemGui(@NotNull Player player, @NotNull String id, String title, InventoryType inventoryType) {
        super(player, id, title, inventoryType);
    }

    public ShopSystemGui(@NotNull Player player, @NotNull String id, Component title, int rows) {
        super(player, id, title, rows);
    }

    public ShopSystemGui(@NotNull Player player, @NotNull String id, Component title, InventoryType inventoryType) {
        super(player, id, title, inventoryType);
    }

    protected void playClickSound() {
        player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1.2f);
    };

}
