package it.einjojo.shopsystem.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.*;
import it.einjojo.shopsystem.ShopSystemPlugin;
import it.einjojo.shopsystem.category.Category;
import it.einjojo.shopsystem.category.CategoryBuilder;
import it.einjojo.shopsystem.item.ShopItem;
import it.einjojo.shopsystem.setup.CategorySetup;
import it.einjojo.shopsystem.shop.CategorizedShop;
import it.einjojo.shopsystem.shop.Shop;
import it.einjojo.shopsystem.shop.ShopFactory;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

@CommandAlias("shopsystem")
public class ShopSystemCommand extends BaseCommand {
    private final ShopSystemPlugin shopSystem;

    public ShopSystemCommand(ShopSystemPlugin shopSystem, PaperCommandManager commandManager) {
        commandManager.getCommandCompletions().registerStaticCompletion("shop-type", Arrays.stream(ShopFactory.ShopType.values()).map(Enum::name).toList());
        commandManager.getCommandContexts().registerContext(ShopFactory.ShopType.class, context -> ShopFactory.ShopType.valueOf(context.popFirstArg()));
        commandManager.getCommandCompletions().registerAsyncCompletion("category", (c) -> shopSystem.getCategoryManager().getCategories().keySet());
        commandManager.getCommandContexts().registerContext(Category.class, context ->
                Optional.ofNullable(shopSystem.getCategoryManager().getCategories().get(context.popFirstArg()))
                        .orElseThrow());
        commandManager.getCommandCompletions().registerAsyncCompletion("shop", (c) ->
                shopSystem.getShopManager().getShops().keySet());
        commandManager.getCommandContexts().registerContext(Shop.class, context ->
                Optional.ofNullable(shopSystem.getShopManager().getShops().get(context.popFirstArg())).orElseThrow());
        commandManager.getCommandCompletions().setDefaultCompletion("category", Category.class);
        commandManager.getCommandCompletions().setDefaultCompletion("shop", Shop.class);
        commandManager.registerCommand(this);
        this.shopSystem = shopSystem;
    }

    @Subcommand("create shop")
    @CommandCompletion("<id> @shop-type <red>BeispielShop")
    public void createShop(Player player, @Single String shopId, ShopFactory.ShopType shopType, String shopName) {
        if (shopSystem.getShopManager().getShops().get(shopId) != null) {
            shopSystem.sendMessage(player, "<prefix><red>Ein Shop mit der ID <id> existiert bereits!",
                    Placeholder.parsed("id", shopId));
            return;
        }
        var shop = new ShopFactory().createShop(shopId, shopSystem.getMiniMessage().deserialize(shopName), shopType);
        shopSystem.getShopManager().registerShop(shop);
        shopSystem.sendMessage(player, "<prefix><gray>Der Shop <red><id></red> vom typ <yellow><type></yellow> wurde erstellt!",
                Placeholder.parsed("id", shopId), Placeholder.parsed("type", shopType.name()));
    }

    @Subcommand("create|setup category")
    @CommandCompletion("@nothing")
    @Description("Startet Setup")
    public void createCategory(Player sender) {
        var setup = new CategorySetup(sender, shopSystem, new CategoryBuilder());
        setup.setOnComplete(category -> {
            shopSystem.getCategoryManager().registerCategory(category);
            shopSystem.sendMessage(sender, "<prefix><gray>Die Kategorie <red><id></red> wurde erstellt!",
                    Placeholder.unparsed("id", category.getInternalName()));
            shopSystem.sendMessage(sender, "<prefix><yellow>Tipp; Weise die Kategorie einem Shop zu! /shopsystem category assign");

        });
        setup.register();
    }


    @Subcommand("category assign")
    @CommandCompletion("@category @shop @nothing")
    @Description("Assign a category to a shop")
    public void assignCategory(CommandSender sender, Category category, Shop shop) {
        if (shop instanceof CategorizedShop categorizedShop) {
            categorizedShop.addCategory(category);
            shopSystem.sendMessage(sender, "<prefix><gray>Die Kategorie <red><category></red> wurde dem Shop <red><shop></red> zugewiesen!",
                    Placeholder.parsed("category", category.getInternalName()), Placeholder.parsed("shop", shop.getId()));
            ;
        } else {
            shopSystem.sendMessage(sender, "<prefix><red>Dieser Shop unterstützt keine Kategorien!");
        }
    }

    @Subcommand("category edit")
    @CommandCompletion("@category @nothing")
    @Description("Opens the setup to modify")
    public void editCategory(Player sender, Category category) {
        var setup = new CategorySetup(sender, shopSystem, category.builder());
        setup.setOnComplete(newCategory -> {
            shopSystem.getCategoryManager().registerCategory(newCategory);
            shopSystem.sendMessage(sender, "<prefix><gray>Die Kategorie <red><id></red> wurde bearbeitet!",
                    Placeholder.unparsed("id", newCategory.getInternalName()));
        });
        setup.register();
    }


    @Subcommand("category removeitems")
    @CommandCompletion("@category @nothing")
    @Description("Opens a gui to remove items from a category")
    public void removeItemFromCategory(Player sender, Category category) {
        throw new UnsupportedOperationException();
    }


    @Subcommand("category additems")
    @CommandCompletion("@category @nothing")
    @Description("Opens a select gui to add items to a category")
    public void addItemToCategory(Player sender, Category category) {
        category.addItem(ShopItem.builder()
                .withItemStack(sender.getInventory().getItemInMainHand())
                .sellPrice(10)
                .buyPrice(20)
                .build());
        shopSystem.sendMessage(sender, "<prefix><gray>Das Item wurde der Kategorie <red><category></red> hinzugefügt!",
                Placeholder.parsed("category", category.getInternalName()));
    }


    @Subcommand("open")
    @CommandCompletion("@shop sell|buy")
    public void openShop(Player sender, Shop shop, @Values("sell|buy") String type) {
        try {
            if (type.equalsIgnoreCase("sell")) {
                shop.openSell(sender);
            } else {
                shop.openBuy(sender);
            }
        } catch (Exception ex) {
            shopSystem.sendMessage(sender, "<prefix><red>Ein Fehler ist aufgetreten: <error>!",
                    Placeholder.parsed("error", ex.getMessage()));
        }
    }


}
