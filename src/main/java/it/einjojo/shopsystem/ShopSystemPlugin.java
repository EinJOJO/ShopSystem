package it.einjojo.shopsystem;

import co.aikar.commands.PaperCommandManager;
import it.einjojo.shopsystem.category.Category;
import it.einjojo.shopsystem.category.CategoryManager;
import it.einjojo.shopsystem.command.ShopSystemCommand;
import it.einjojo.shopsystem.config.ShopSystemPluginConfig;
import it.einjojo.shopsystem.item.ItemStackTradeHandler;
import it.einjojo.shopsystem.item.ShopItemBuilder;
import it.einjojo.shopsystem.shop.CategorizedShop;
import it.einjojo.shopsystem.shop.ShopManager;
import mc.obliviate.inventory.InventoryAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.List;

public class ShopSystemPlugin extends JavaPlugin {
    private static volatile ShopSystemPlugin instance;
    private ShopSystemPluginConfig config;
    private ShopManager shopManager;
    private CategoryManager categoryManager;
    private MiniMessage miniMessage;

    public static ShopSystemPlugin getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Plugin not enabled yet");
        }
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        new InventoryAPI(this).init();
        config = new ShopSystemPluginConfig(this);
        shopManager = new ShopManager();
        categoryManager = new CategoryManager();
        miniMessage = createMiniMessage(MiniMessage.miniMessage().deserialize(config.getMiniMessagePrefix()));
        sendStartupMessage();
        registerCommands();
        loadTestEnvironment();

    }

    private void loadTestEnvironment() {
        categoryManager.registerCategory(new Category("test", List.of(new ShopItemBuilder()
                .item(new ItemStackTradeHandler(new ItemStack(Material.ACACIA_PLANKS)))
                .buyPrice(200)
                .sellPrice(100)
                .build())));
        var shop = new CategorizedShop("testshop");
        shop.addCategory(categoryManager.getCategories().get("test"));
        shopManager.registerShop(shop);

    }

    private void registerCommands() {
        var paperManager = new PaperCommandManager(this);
        new ShopSystemCommand(this, paperManager);
    }

    private MiniMessage createMiniMessage(ComponentLike prefix) {
        return MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(TagResolver.standard())
                        .tag("prefix", Tag.selfClosingInserting(prefix))
                        .build())
                .build();
    }

    private void sendStartupMessage() {
        CommandSender c = getServer().getConsoleSender();
        c.sendMessage(deserializeWithPrimaryStyle("  .----..-. .-. .----. .----.  .----..-.  .-..----..---. .----..-.   .-."));
        c.sendMessage(deserializeWithPrimaryStyle(" { {__  | {_} |/  {}  \\| {}  }{ {__   \\ \\/ /{ {__ {_   _}| {_  |  `.'  |"));
        c.sendMessage(deserializeWithPrimaryStyle(" .-._} }| { } |\\      /| .--' .-._} }  }  { .-._} } | |  | {__ | |\\ /| |"));
        c.sendMessage(deserializeWithPrimaryStyle(" `----' `-' `-' `----' `-'    `----'   `--' `----'  `-'  `----'`-' ` `-'"));
        c.sendMessage("");
        c.sendMessage(deserializeWithPrimaryStyle("<gray>ShopSystem</gray> v" + getPluginMeta().getVersion() + "<green> enabled.</green> <gray>- Created by </gray>EinJoJo"));

    }

    private Component deserializeWithPrimaryStyle(String s) {
        return miniMessage.deserialize(MessageFormat.format(config.getPrimaryStyle(), s));
    }

    @Override
    public void onDisable() {

    }

    @NotNull
    public ShopSystemPluginConfig getShopConfig() {
        return config;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }

    public CategoryManager getCategoryManager() {
        return categoryManager;
    }
}
