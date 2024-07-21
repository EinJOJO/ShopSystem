package it.einjojo.shopsystem.setup;

import it.einjojo.shopsystem.ShopSystemPlugin;
import it.einjojo.shopsystem.item.ShopItem;
import it.einjojo.shopsystem.item.ShopItemBuilder;
import org.bukkit.entity.Player;

//TODO
public class ShopItemSetup extends AbstractSetup<ShopItem> {

    protected ShopItemSetup(ShopSystemPlugin plugin, Player player, ShopItemBuilder itemBuilder) {
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
