package it.einjojo.shopsystem.setup;

import org.bukkit.inventory.ItemStack;

public interface SetupStage<SetupInstance> {
    void prompt(SetupInstance setup);


}
