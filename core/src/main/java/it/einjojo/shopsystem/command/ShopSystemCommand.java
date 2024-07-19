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
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

@CommandAlias("shopsystem")
public class ShopSystemCommand extends BaseCommand {
    private final ShopSystemPlugin shopSystem;

    public ShopSystemCommand(ShopSystemPlugin shopSystem, PaperCommandManager commandManager) {
        commandManager.getCommandCompletions().registerStaticCompletion("shop-type", Arrays.stream(ShopFactory.ShopType.values()).map(Enum::name).toList());
        commandManager.getCommandContexts().registerContext(ShopFactory.ShopType.class, context -> {
            return ShopFactory.ShopType.valueOf(context.popFirstArg());
        });
        commandManager.getCommandCompletions().registerAsyncCompletion("category", (c) -> shopSystem.getCategoryManager().getCategories().keySet());
        commandManager.getCommandContexts().registerContext(Category.class, context -> {
            return Optional.ofNullable(shopSystem.getCategoryManager().getCategories().get(context.popFirstArg())).orElseThrow();
        });
        commandManager.getCommandCompletions().registerAsyncCompletion("shop", (c) -> shopSystem.getShopManager().getShops().keySet());
        commandManager.getCommandContexts().registerContext(Shop.class, context ->
                Optional.ofNullable(shopSystem.getShopManager().getShops().get(context.popFirstArg())).orElseThrow());


        commandManager.registerCommand(this);
        this.shopSystem = shopSystem;
    }

    @Subcommand("create shop")
    @CommandCompletion("<id> @shop-type <red>BeispielShop")
    public void createShop(Player player, @Single String shopId, ShopFactory.ShopType shopType, String shopName) {
        if (shopSystem.getShopManager().getShops().get(shopId) != null) {
            player.sendMessage(shopSystem.getMiniMessage().deserialize("<prefix><red>Ein Shop mit der ID <id> existiert bereits!",
                    Placeholder.parsed("id", shopId)));
            return;
        }
        var shop = new ShopFactory().createShop(shopId, shopSystem.getMiniMessage().deserialize(shopName), shopType);
        shopSystem.getShopManager().registerShop(shop);
        player.sendMessage(shopSystem.getMiniMessage().deserialize("<prefix><gray>Der Shop <red><id></red> vom typ <yellow><type></yellow> wurde erstellt!",
                Placeholder.parsed("id", shopId), Placeholder.parsed("type", shopType.name())));
    }

    @Subcommand("create|setup category")
    @CommandCompletion("@nothing")
    public void createCategory(Player sender) {
        var setup = new CategorySetup(sender, shopSystem, new CategoryBuilder());
        setup.setOnComplete(category -> {
            shopSystem.getCategoryManager().registerCategory(category);
            sender.sendMessage(shopSystem.getMiniMessage().deserialize("<prefix><gray>Die Kategorie <red><id></red> wurde erstellt!",
                    Placeholder.unparsed("id", category.getName())));
        });
        setup.register();


    }

    @Subcommand("category assign")
    @CommandCompletion("@category @shop @nothing")
    @Description("Assign a category to a shop")
    public void assignCategory(Player sender, Category category, Shop shop) {
        if (shop instanceof CategorizedShop categorizedShop) {
            categorizedShop.addCategory(category);
            sender.sendMessage(shopSystem.getMiniMessage().deserialize("<prefix><gray>Die Kategorie <red><category></red> wurde dem Shop <red><shop></red> zugewiesen!",
                    Placeholder.parsed("category", category.getName()), Placeholder.parsed("shop", shop.getId())));
        } else {
            sender.sendMessage(shopSystem.getMiniMessage().deserialize("<prefix><red>Dieser Shop unterstützt keine Kategorien!"));
        }
    }

    @Subcommand("category additem")
    @CommandCompletion("@category @nothing")
    @Description("Add an itemstack to a category")
    public void addItemToCategory(Player sender, Category category) {
        category.addItem(ShopItem.builder()
                .withItemStack(sender.getInventory().getItemInMainHand())
                .sellPrice(10)
                .buyPrice(20)
                .build());
        sender.sendMessage(shopSystem.getMiniMessage().deserialize("<prefix><gray>Das Item wurde der Kategorie <red><category></red> hinzugefügt!",
                Placeholder.parsed("category", category.getName())));
    }


    @Subcommand("open")
    @CommandCompletion("@shop @nothing")
    public void openShop(Player sender, Shop shop, @Values("sell|buy") String type) {
        try {
            if (type.equalsIgnoreCase("sell")) {
                shop.openSell(sender);
            } else {
                shop.openBuy(sender);
            }
        } catch (Exception ex) {
            sender.sendMessage(shopSystem.getMiniMessage().deserialize("<prefix><red>Ein Fehler ist aufgetreten: <error>!",
                    Placeholder.parsed("error", ex.getMessage())));
        }
    }


}
