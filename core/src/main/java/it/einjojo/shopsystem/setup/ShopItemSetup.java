package it.einjojo.shopsystem.setup;

import it.einjojo.shopsystem.ShopSystemPlugin;
import it.einjojo.shopsystem.item.ShopItem;
import org.bukkit.entity.Player;

public class ShopItemSetup extends AbstractSetup<ShopItem> {
    protected ShopItemSetup(ShopSystemPlugin plugin, Player player) {
        super(plugin, player);
    }

    @Override
    protected void postRegister() {

    }

    @Override
    protected void complete() {

    }


    private interface ISetupStage extends SetupStage<ShopItemSetup> {
    }
}
