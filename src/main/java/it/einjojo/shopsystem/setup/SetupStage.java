package it.einjojo.shopsystem.setup;

import org.bukkit.inventory.ItemStack;

public interface SetupStage<SetupInstance> {
    void prompt(SetupInstance setup);

    default void handleChatInput(SetupInstance setup, String input) {
    }

    default void handleItemDrop(SetupInstance setup, ItemStack dropped) {
    }
}
