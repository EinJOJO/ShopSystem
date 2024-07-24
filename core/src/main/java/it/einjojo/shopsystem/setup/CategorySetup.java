package it.einjojo.shopsystem.setup;

import io.papermc.paper.event.player.AsyncChatEvent;
import it.einjojo.shopsystem.ShopSystemPlugin;
import it.einjojo.shopsystem.category.Category;
import it.einjojo.shopsystem.category.CategoryBuilder;
import it.einjojo.shopsystem.item.ShopItem;
import it.einjojo.shopsystem.item.ShopItemBuilder;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Setup for creating a new category.
 * Call register() to start the setup.
 */
public class CategorySetup extends AbstractSetup<Category> {
    private @NotNull CategorySetup.CategorySetupStage current;
    private final CategoryBuilder categoryBuilder;

    public CategorySetup(Player player, ShopSystemPlugin plugin, CategoryBuilder categoryBuilder) {
        super(plugin, player);
        this.categoryBuilder = categoryBuilder;
        if (categoryBuilder.getInternalName() == null) {
            current = Stage.INTERNAL_NAME;
        } else {
            current = Stage.ACTION_SELECT;
        }
    }

    private void setCurrentAndPrompt(@NotNull CategorySetup.CategorySetupStage current) {
        this.current = current;
        current.prompt(this);
    }

    @Override
    protected void complete() {
        unregister();
        Category buildResult;
        try {
            buildResult = categoryBuilder.build();
        } catch (Exception exception) {
            String message = exception.getMessage();
            sendMessage("<red>Ein Fehler ist aufgreten: <ex>",
                    Placeholder.unparsed("ex", message == null ? exception.getClass().getSimpleName() : message));
            throw exception;
        }
        sendMessage("<green>Setup abgeschlossen.");
        callCompletionConsumer(buildResult);
    }

    @Override
    protected String getMessagePrefix() {
        return "<dark_gray>[<red>Category-Setup</red>] <gray>";
    }

    @Override
    protected void postRegister() {
        player.sendMessage(" ");
        current.prompt(this);
    }


    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (event.getPlayer().getUniqueId().equals(player.getUniqueId())) {
            String message = PlainTextComponentSerializer.plainText().serialize(event.originalMessage());
            if (checkCancel(message)) return;
            if (current.handleChatInput(this, message)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent dropItemEvent) {
        if (dropItemEvent.getPlayer().getUniqueId().equals(player.getUniqueId())) {
            if (current.handleItemDrop(this, dropItemEvent.getItemDrop().getItemStack())) {
                dropItemEvent.setCancelled(true);
            }
            ;
        }
    }

    private interface CategorySetupStage extends SetupStage<CategorySetup> {
        /**
         * @param setup setup instance
         * @param input player message
         * @return true if the event should be cancelled
         */
        default boolean handleChatInput(CategorySetup setup, String input) {
            return false;
        }

        /**
         * @param setup   setup instance
         * @param dropped item that the player dropped
         * @return if the event should be cancelled
         */
        default boolean handleItemDrop(CategorySetup setup, ItemStack dropped) {
            return false;
        }
    }

    private enum Stage implements CategorySetupStage {
        INTERNAL_NAME {
            @Override
            public void prompt(CategorySetup setup) {
                setup.sendMessage("<red>Bitte einen <b>einzigartigen internen Namen</b> der Kategorie ein");
                setup.sendMessage("<gray>z.B 'redstone'");
            }

            @Override
            public boolean handleChatInput(CategorySetup setup, String input) {
                setup.categoryBuilder.internalName(input);
                setup.confirmInput(input);
                setup.predicateOrActionSelect(builder -> builder.getDisplayName() == null, NAME);
                return true;
            }
        },
        ACTION_SELECT {
            @Override
            public void prompt(CategorySetup s) {
                s.sendMessage("Was möchtest du tun?");
                s.sendMessage("<red>1<gray> - Anzeigenamen ändern");
                s.sendMessage("<red>2<gray> - Beschreibung ändern");
                s.sendMessage("<red>3<gray> - Material ändern");
                s.sendMessage("<red>4<gray> - Existierende Items hinzufügen");
                s.sendMessage("<red>5<gray> - Items entfernen");
                s.sendMessage("<red>6<gray> - Item erstellen");
                s.sendMessage("<red>0<gray> - Beenden");
                s.sendMessage("<red>Gib die Nummer ein, um fortzufahren");
            }

            @Override
            public boolean handleChatInput(CategorySetup setup, String input) {
                CategorySetupStage nextStage;
                switch (input) {
                    case "1":
                        nextStage = NAME;
                        break;
                    case "2":
                        nextStage = DESCRIPTION;
                        break;
                    case "3":
                        nextStage = MATERIAL;
                        break;
                    case "4":
                        nextStage = ITEM_EXISTING_ADD;
                        break;
                    case "5":
                        nextStage = ITEM_REMOVE;
                        break;
                    case "6":
                        nextStage = new ShopItemSetupStage(setup);
                        // TODO START ITEM SETUP
                        break;
                    case "0":
                        setup.complete();
                        return true;
                    default:
                        setup.sendMessage("<red>Ungültige Eingabe.");
                        prompt(setup);
                        return true;
                }
                setup.confirmInput(input);
                setup.setCurrentAndPrompt(nextStage);
                return true;
            }
        },
        NAME {
            @Override
            public void prompt(CategorySetup setup) {
                setup.sendMessage("<red>Bitte gib den Anzeigenamen der Kategorie ein");
                setup.sendMessage("<gray>z.B <format>", Placeholder.unparsed("format", "<red>Redstone</red>"));
            }

            @Override
            public boolean handleChatInput(CategorySetup setup, String input) {
                setup.categoryBuilder.displayName(setup.getPlugin().getMiniMessage().deserialize(input));
                setup.confirmInput(input);
                setup.predicateOrActionSelect(builder -> builder.getDescription() == null || builder.getDescription().isEmpty(), DESCRIPTION);
                return true;
            }
        },
        DESCRIPTION {
            @Override
            public void prompt(CategorySetup setup) {
                setup.sendMessage("<red>Bitte gib die Beschreibung der Kategorie ein");
                setup.sendMessage("<gray>z.B \"Alles rund um Redstone\"");
            }

            @Override
            public boolean handleChatInput(CategorySetup setup, String input) {
                setup.categoryBuilder.description(input);
                setup.confirmInput(input);
                setup.predicateOrActionSelect(builder -> builder.getDisplayMaterial() == null, MATERIAL);
                return true;
            }
        },
        MATERIAL {
            @Override
            public void prompt(CategorySetup setup) {
                setup.sendMessage("<red>Droppe ein Item, was das Icon der Kategorie sein soll");
            }

            @Override
            public boolean handleItemDrop(CategorySetup setup, ItemStack dropped) {
                setup.categoryBuilder.displayMaterial(dropped.getType());
                setup.confirmInput(dropped.getType().name());
                setup.setCurrentAndPrompt(ACTION_SELECT);
                return true;
            }
        },
        ITEM_EXISTING_ADD {
            @Override
            public void prompt(CategorySetup setup) {
                //TODO open select gui
            }
        },
        ITEM_REMOVE {
            @Override
            public void prompt(CategorySetup setup) {
                List<ShopItem> itemList = setup.categoryBuilder.getItemList();
                int i = 1;
                for (ShopItem item : itemList) {
                    setup.sendMessage("<red><index><gray> - <dark_gray><item>",
                            Placeholder.unparsed("index", String.valueOf(i++)),
                            Placeholder.unparsed("item", item.toString())); //TODO NBT API String representation
                }
                setup.sendMessage("<yellow>Gib den Index an, um das Item zu entfernen.");
            }

            @Override
            public boolean handleChatInput(CategorySetup setup, String input) {
                try {
                    int index = Integer.parseInt(input) - 1;
                    List<ShopItem> items = new ArrayList<>(setup.categoryBuilder.getItemList());
                    if (index < 0 || index >= items.size()) {
                        setup.sendMessage("<red>Ungültiger Wertebereich. Versuche es nochmal!");
                        return true;
                    }
                    ShopItem removed = items.remove(index);
                    setup.categoryBuilder.setItemList(items);
                    setup.sendMessage("<gray>Das <hover:show_text:'<gray><item-string></gray>'>Item</hover> <red><index></red> wurde entfernt",
                            Placeholder.unparsed("item-string", removed.toString()),
                            Placeholder.unparsed("index", String.valueOf(index + 1)));
                    setup.setCurrentAndPrompt(Stage.ACTION_SELECT);
                } catch (NumberFormatException ex) {
                    setup.sendMessage("<red>Gib eine Zahl an.");
                }
                return true;
            }
        }
    }

    private static class ShopItemSetupStage implements CategorySetupStage {
        private final CategorySetup parent;
        private final ShopItemSetup itemSetup;

        public ShopItemSetupStage(CategorySetup parent) {
            this.parent = parent;
            itemSetup = new ShopItemSetup(parent.getPlugin(), Bukkit.getPlayer(parent.getPlayerUuid()), new ShopItemBuilder());
            itemSetup.setOnComplete((shopitem) -> {
                parent.categoryBuilder.addItem(shopitem);
                parent.current = Stage.ACTION_SELECT;
                parent.register(); // post-register will prompt;
            });
        }

        @Override
        public void prompt(CategorySetup ignore) {
            parent.sendMessage("<yellow>Erstelle ein neues Item für die Kategorie");
            parent.unregister();
            itemSetup.register();
        }
    }


    private void predicateOrActionSelect(Predicate<CategoryBuilder> setupPredicate, CategorySetupStage setupStage) {
        if (setupPredicate.test(this.categoryBuilder)) {
            setCurrentAndPrompt(setupStage);
        } else {
            setCurrentAndPrompt(Stage.ACTION_SELECT);
        }
    }
}



