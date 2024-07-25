package it.einjojo.shopsystem.setup;

import de.tr7zw.changeme.nbtapi.NBT;
import io.papermc.paper.event.player.AsyncChatEvent;
import it.einjojo.shopsystem.ShopSystemPlugin;
import it.einjojo.shopsystem.item.ShopItem;
import it.einjojo.shopsystem.item.ShopItemBuilder;
import it.einjojo.shopsystem.item.handler.ItemStackTradeHandler;
import it.einjojo.shopsystem.item.handler.TradeHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

//TODO
public class ShopItemSetup extends AbstractSetup<ShopItem> {
    private final ShopItemBuilder builder;
    private @NotNull ISetupStage currentStage;

    protected ShopItemSetup(ShopSystemPlugin plugin, Player player, ShopItemBuilder itemBuilder) {
        super(plugin, player);
        builder = itemBuilder;
        if (itemBuilder.getTradeHandler() == null) {

        }
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

        void handleDrop(ShopItemSetup setup, ItemStack dropped, PlayerDropItemEvent cancelledEvent);
    }


    enum Stage implements ISetupStage {
        ACTION_SELECT {
            private static final Component NOT_AVAILABLE = Component.text("N/A", NamedTextColor.RED);

            @Override
            public void prompt(ShopItemSetup setup) {
                ShopItemBuilder builder = setup.getBuilder();
                setup.sendMessage("<yellow>Gib eine Zahl an");
                setup.sendMessage("<green>1 <gray>- TradeHandler ändern <dark_gray>(current: <gray><current>)",
                        currentNotNull(builder.getTradeHandler(), tradeHandler -> tradeHandler.getClass().getSimpleName()),
                        tradeHandlerMeta(builder.getTradeHandler()));
                setup.sendMessage("<green>2 <gray>- DisplayItem ändern <dark_gray>(<hover:show_text:'<meta>'>current: <gray><current></hover>)",
                        currentNotNull(builder.getDisplayItem(), item -> item.getType().name()),
                        Placeholder.unparsed("meta", builder.getDisplayItem() == null ? "N/A" : NBT.itemStackToNBT(builder.getDisplayItem()).toString()));
                setup.sendMessage("<green>3 <gray>- BuyPrice ändern <dark_gray>(current: <gray><current>)",
                        currentNotNull(builder.getBuyPrice(), Object::toString));
                setup.sendMessage("<green>4 <gray>- SellPrice ändern <dark_gray>(current: <gray><current>)",
                        currentNotNull(builder.getSellPrice(), Object::toString));
                setup.sendMessage("<green>5 <gray>- Lagerbestand ändern <dark_gray>(current: <gray><current>)",
                        currentNotNull(builder.getStock(), Object::toString));
                setup.sendMessage("<gray>Informationen:");
                setup.sendMessage("<dark_gray> - <gray>Observer: <dark_gray>(current: <gray><current>)",
                        currentNotNull(builder.getObserver(), observer -> observer.getClass().getSimpleName()));
            }

            private <T> TagResolver currentNotNull(@Nullable T object, Function<T, String> extractor) {
                return object != null ? Placeholder.unparsed("current", extractor.apply(object)) : Placeholder.component("current", NOT_AVAILABLE);
            }

            private TagResolver tradeHandlerMeta(@Nullable TradeHandler tradeHandler) {
                if (tradeHandler instanceof ItemStackTradeHandler itemStackTradeHandler) {
                    return Placeholder.component("meta", Component.text("ItemStack: ", NamedTextColor.GREEN)
                            .appendNewline()
                            .append(Component.text(NBT.itemStackToNBT(itemStackTradeHandler.getItemStack()).toString(), NamedTextColor.GRAY)));
                } else {
                    return Placeholder.component("meta", NOT_AVAILABLE);
                }
            }


            @Override
            public void handleChat(ShopItemSetup setup, String message, AsyncChatEvent cancelledEvent) {
                ISetupStage stage = null;
                switch (message) {
                    case "1" -> {
                        stage = TRADE_HANDLER_ITEM;
                    }
                    case "2" -> {
                        stage = DISPLAY_ITEM;
                    }
                    case "3" -> {
                        stage = BUY_PRICE;
                    }
                    case "4" -> {
                        stage = SELL_PRICE;
                    }
                    case "5" -> {
                        stage = STOCK;
                    }
                    default -> {
                        setup.sendMessage("<red>Ungültige Eingabe!");
                        return;
                    }
                }
                setup.confirmInput(message);
                setup.setCurrentAndPrompt(stage);
            }

            @Override
            public void handleDrop(ShopItemSetup setup, ItemStack dropped, PlayerDropItemEvent cancelledEvent) {
                cancelledEvent.setCancelled(false);
            }
        },
        DISPLAY_ITEM {
            @Override
            public void handleChat(ShopItemSetup setup, String message, AsyncChatEvent cancelledEvent) {
                cancelledEvent.setCancelled(false);
            }

            @Override
            public void handleDrop(ShopItemSetup setup, ItemStack dropped, PlayerDropItemEvent cancelledEvent) {
                if (dropped.getType().isAir()) {
                    setup.sendMessage("<red>Item darf nicht Luft sein.");
                    return;
                }
                setup.getBuilder().displayItem(dropped);
                setup.confirmInput(dropped.getType().name());
            }

            @Override
            public void prompt(ShopItemSetup setup) {
                setup.sendMessage("<yellow>Droppe das Item, das im Shop zu sehen sein wird.");
                setup.sendMessage("<gray>Das Item kann sich vom TradeHandler unterscheiden.");
            }
        },
        BUY_PRICE {
            @Override
            public void handleChat(ShopItemSetup setup, String message, AsyncChatEvent cancelledEvent) {
                try {
                    int price = Integer.parseInt(message);
                    if (price == -1) {
                        setup.getBuilder().buyPrice(null);
                        setup.confirmInput(message);
                    } else {
                        setup.getBuilder().buyPrice(price);
                        setup.confirmInput("nicht verkaufbar.");
                    }
                    setup.setCurrentAndPrompt(Stage.ACTION_SELECT);
                } catch (NumberFormatException ex) {
                    setup.sendMessage("<red>Ungültige Eingabe! -1 & Positive ganze Zahlen");
                }
            }

            @Override
            public void handleDrop(ShopItemSetup setup, ItemStack dropped, PlayerDropItemEvent cancelledEvent) {
                cancelledEvent.setCancelled(false);
            }

            @Override
            public void prompt(ShopItemSetup setup) {
                setup.sendMessage("<yellow>Gib den Kaufpreis an oder -1 für nicht verkaufbar.");
            }
        },
        SELL_PRICE {
            @Override
            public void handleChat(ShopItemSetup setup, String message, AsyncChatEvent cancelledEvent) {
                try {
                    int price = Integer.parseInt(message);
                    if (price == -1) {
                        setup.getBuilder().sellPrice(null);
                        setup.confirmInput(message);
                    } else {
                        setup.getBuilder().sellPrice(price);
                        setup.confirmInput("nicht verkaufbar.");
                    }
                    setup.setCurrentAndPrompt(Stage.ACTION_SELECT);
                } catch (NumberFormatException ex) {
                    setup.sendMessage("<red>Ungültige Eingabe! -1 & Positive ganze Zahlen");
                }
            }

            @Override
            public void handleDrop(ShopItemSetup setup, ItemStack dropped, PlayerDropItemEvent cancelledEvent) {
                cancelledEvent.setCancelled(false);
            }

            @Override
            public void prompt(ShopItemSetup setup) {
                setup.sendMessage("<yellow>Gib den Verkaufspreis an oder -1 für nicht verkaufbar.");
            }
        },
        STOCK {
            @Override
            public void handleChat(ShopItemSetup setup, String message, AsyncChatEvent cancelledEvent) {
                try {
                    int stock = Integer.parseInt(message);
                    if (stock == -1) {
                        setup.getBuilder().stock(null);
                        setup.confirmInput(message);
                    } else {
                        setup.getBuilder().stock(stock);
                        setup.confirmInput("unendlich.");
                    }
                    setup.setCurrentAndPrompt(Stage.ACTION_SELECT);
                } catch (NumberFormatException ex) {
                    setup.sendMessage("<red>Ungültige Eingabe! -1 & Positive ganze Zahlen");
                }
            }

            @Override
            public void handleDrop(ShopItemSetup setup, ItemStack dropped, PlayerDropItemEvent cancelledEvent) {
                cancelledEvent.setCancelled(false);
            }

            @Override
            public void prompt(ShopItemSetup setup) {
                setup.sendMessage("<yellow>Gib den Lagerbestand an oder -1 für unendlich.");
            }
        },
        TRADE_HANDLER_SELECTION { //TODO create more handler

            @Override
            public void handleChat(ShopItemSetup setup, String message, AsyncChatEvent cancelledEvent) {

            }

            @Override
            public void handleDrop(ShopItemSetup setup, ItemStack dropped, PlayerDropItemEvent cancelledEvent) {
                cancelledEvent.setCancelled(false);
            }

            @Override
            public void prompt(ShopItemSetup setup) {
                setup.sendMessage("Tradehandler bestimmmen das Item, dass verkauft/gekauft wird");
            }
        },
        TRADE_HANDLER_ITEM {
            @Override
            public void handleChat(ShopItemSetup setup, String message, AsyncChatEvent cancelledEvent) {
                cancelledEvent.setCancelled(false);
            }

            @Override
            public void handleDrop(ShopItemSetup setup, ItemStack dropped, PlayerDropItemEvent cancelledEvent) {
                if (dropped.getType().isAir()) {
                    setup.sendMessage("<red>Item darf nicht Luft sein.");
                    return;
                }
                setup.getBuilder().withItemStack(dropped);
                setup.confirmInput(dropped.getType().name());
                setup.setCurrentAndPrompt(Stage.ACTION_SELECT);
            }

            @Override
            public void prompt(ShopItemSetup setup) {
                setup.sendMessage("<yellow>Droppe ein Itemstack.");

            }
        };
    }
}