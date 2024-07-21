package it.einjojo.shopsystem.setup;

import io.papermc.paper.event.player.AsyncChatEvent;
import it.einjojo.shopsystem.ShopSystemPlugin;
import it.einjojo.shopsystem.item.ShopItem;
import it.einjojo.shopsystem.item.ShopItemBuilder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

//TODO
public class ShopItemSetup extends AbstractSetup<ShopItem> {
    private final ShopItemBuilder builder;
    private @NotNull ISetupStage currentStage;

    protected ShopItemSetup(ShopSystemPlugin plugin, Player player, ShopItemBuilder itemBuilder) {
        super(plugin, player);
        builder = itemBuilder;
    }

    @Override
    protected void postRegister() {
        currentStage.prompt(this);
    }

    @Override
    protected void complete() {

    }

    @Override
    protected String getMessagePrefix() {
        return "<dark_gray>[<green>Item-Setup</green>] <gray>";
    }


    protected void setCurrentAndPrompt(ISetupStage stage) {
        currentStage = stage;
        stage.prompt(this);
    }

    public ShopItemBuilder getBuilder() {
        return builder;
    }

    protected interface ISetupStage extends SetupStage<ShopItemSetup> {
        void handleChat(ShopItemSetup setup, String message, AsyncChatEvent cancelledEvent);
        void handleDrop(ShopItemSetup setup, String message, PlayerDropItemEvent cancelledEvent);
    }


    enum Stage implements ISetupStage {
        ACTION_SELECT {
            @Override
            public void prompt(ShopItemSetup setup) {
                setup.sendMessage("<yellow>Gib eine Zahl an");
                setup.sendMessage("1. Information ausgeben");
            }

            @Override
            public void handleChat(ShopItemSetup setup, String message, AsyncChatEvent cancelledEvent) {

            }

            @Override
            public void handleDrop(ShopItemSetup setup, String message, PlayerDropItemEvent cancelledEvent) {
                cancelledEvent.setCancelled(false);
            }
        }
    }
}
