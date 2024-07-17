package it.einjojo.shopsystem.setup;

import io.papermc.paper.event.player.AsyncChatEvent;
import it.einjojo.shopsystem.ShopSystemPlugin;
import it.einjojo.shopsystem.category.Category;
import it.einjojo.shopsystem.category.CategoryBuilder;
import it.einjojo.shopsystem.item.ShopItem;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class CategorySetup extends AbstractSetup<Category> {
    private @NotNull CategorySetup.ISetupStage current;
    private final CategoryBuilder categoryBuilder;

    public CategorySetup(Player player, ShopSystemPlugin plugin, CategoryBuilder categoryBuilder) {
        super(plugin, player);
        this.categoryBuilder = categoryBuilder;
        if (categoryBuilder.getName() == null) {
            current = Stage.NAME;
        } else {
            current = Stage.ACTION_SELECT;
        }
    }

    private void setCurrentAndPrompt(@NotNull CategorySetup.ISetupStage current) {
        this.current = current;
        current.prompt(this);
    }

    @Override
    protected void complete() {
        unregister();
        callCompletionConsumer(null);
    }

    public void sendMessage(String message, TagResolver... resolvers) {
        player.sendMessage(getPlugin().getMiniMessage().deserialize(
                "<dark_gray>[<red>Category-Setup</red>]</dark_gray> <gray>" + message, resolvers));
    }

    @Override
    protected void postRegister() {
        player.sendMessage(" ");
        current.prompt(this);
    }


    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (event.getPlayer().getUniqueId().equals(player.getUniqueId())) {
            event.setCancelled(true);
            String message = PlainTextComponentSerializer.plainText().serialize(event.originalMessage());
            if (message.equalsIgnoreCase("cancel")) {
                unregister();
                sendMessage("<red>Setup abgebrochen.");
                return;
            }
            current.handleChatInput(this, message);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent dropItemEvent) {
        if (dropItemEvent.getPlayer().getUniqueId().equals(player.getUniqueId())) {
            dropItemEvent.setCancelled(true);
            current.handleItemDrop(this, dropItemEvent.getItemDrop().getItemStack());
        }
    }

    private interface ISetupStage extends SetupStage<CategorySetup> {
    }

    private enum Stage implements ISetupStage {
        ACTION_SELECT {
            @Override
            public void prompt(CategorySetup s) {
                s.sendMessage("Was möchtest du tun?");
                s.sendMessage("<red>1<gray> - Anzeigenamen ändern");
                s.sendMessage("<red>2<gray> - Beschreibung ändern");
                s.sendMessage("<red>3<gray> - Material ändern");
                s.sendMessage("<red>4<gray> - Items hinzufügen");
                s.sendMessage("<red>5<gray> - Items entfernen");
                s.sendMessage("<red>6<gray> - Beenden");
                s.sendMessage("<red>Gib die Nummer ein, um fortzufahren");
            }

            public void handleChatInput(String input, CategorySetup setup) {
                switch (input) {
                    case "1":
                        setup.setCurrentAndPrompt(NAME);
                        break;
                    case "2":
                        setup.setCurrentAndPrompt(DESCRIPTION);
                        break;
                    case "3":
                        setup.setCurrentAndPrompt(MATERIAL);
                        break;
                    case "4":
                        setup.setCurrentAndPrompt(new ProxiedShopItemSetupStage());
                        break;
                    case "5":
                        setup.setCurrentAndPrompt(ITEM_REMOVE);
                        break;
                    case "6":
                        setup.complete();
                        break;
                    default:
                        setup.sendMessage("<red>Ungültige Eingabe.");
                        prompt(setup);
                }
            }
        },
        NAME {
            @Override
            public void prompt(CategorySetup setup) {
                setup.sendMessage("<red>Bitte gib den Anzeigenamen der Kategorie ein");
                setup.sendMessage("<gray>z.B <format>", Placeholder.unparsed("format", "<red>Redstone</red>"));
            }

            @Override
            public void handleChatInput(CategorySetup setup, String input) {
                setup.categoryBuilder.setDisplayName(setup.getPlugin().getMiniMessage().deserialize(input));
                setup.predicateOrActionSelect(builder -> builder.getDescription() == null, MATERIAL);
            }
        },
        DESCRIPTION {
            @Override
            public void prompt(CategorySetup setup) {
                setup.sendMessage("<red>Bitte gib die Beschreibung der Kategorie ein");
                setup.sendMessage("<gray>z.B \"Alles rund um Redstone\"");
            }

            @Override
            public void handleChatInput(CategorySetup setup, String input) {
                setup.categoryBuilder.setDescription(input);
                setup.predicateOrActionSelect(builder -> builder.getDisplayMaterial() == null, MATERIAL);
            }
        },
        MATERIAL {
            @Override
            public void prompt(CategorySetup setup) {
                setup.sendMessage("<red>Droppe ein Item, was das Icon der Kategorie sein soll");
            }

            @Override
            public void handleItemDrop(CategorySetup setup, ItemStack dropped) {
                setup.categoryBuilder.setDisplayMaterial(dropped.getType());
                setup.setCurrentAndPrompt(ACTION_SELECT);
            }
        },
        ITEM_REMOVE {
            @Override
            public void prompt(CategorySetup setup) {
                List<ShopItem> itemList = setup.categoryBuilder.getItemList();
                int i = 0;
                for (ShopItem item : itemList) {
                    setup.sendMessage("<red><index><gray> - <dark_gray><item>",
                            Placeholder.unparsed("index", String.valueOf(i + 1)),
                            Placeholder.unparsed("item", item.toString()));
                }
            }
        }
    }

    private static class ProxiedShopItemSetupStage extends ShopItemSetupStage<CategorySetup> {

        @Override
        public void prompt(CategorySetup setup) {

        }

        @Override
        public void handleChatInput(CategorySetup setup, String input) {

        }

        @Override
        public void handleItemDrop(CategorySetup setup, ItemStack dropped) {

        }
    }


    private void predicateOrActionSelect(Predicate<CategoryBuilder> setupPredicate, ISetupStage setupStage) {
        if (setupPredicate.test(this.categoryBuilder)) {
            setCurrentAndPrompt(setupStage);
        } else {
            setCurrentAndPrompt(Stage.ACTION_SELECT);
        }
    }
}



