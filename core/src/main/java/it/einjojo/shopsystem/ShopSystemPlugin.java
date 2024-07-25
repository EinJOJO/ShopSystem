package it.einjojo.shopsystem;

import co.aikar.commands.PaperCommandManager;
import it.einjojo.shopsystem.category.CategoryBuilder;
import it.einjojo.shopsystem.category.CategoryManager;
import it.einjojo.shopsystem.command.ShopSystemCommand;
import it.einjojo.shopsystem.config.ShopSystemPluginConfig;
import it.einjojo.shopsystem.economy.EconomyHandler;
import it.einjojo.shopsystem.economy.EconomyHandlerFactory;
import it.einjojo.shopsystem.item.ShopItemBuilder;
import it.einjojo.shopsystem.shop.CategorizedShop;
import it.einjojo.shopsystem.shop.ShopManager;
import it.einjojo.shopsystem.util.Messages;
import mc.obliviate.inventory.InventoryAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

public class ShopSystemPlugin extends JavaPlugin {
    private static volatile ShopSystemPlugin instance;
    private ShopSystemPluginConfig config;
    private ShopManager shopManager;
    private EconomyHandler economyHandler;
    private CategoryManager categoryManager;

    public static ShopSystemPlugin getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Plugin not enabled yet");
        }
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        economyHandler = new EconomyHandlerFactory().createEconomyHandler();
        config = new ShopSystemPluginConfig(this);
        new Messages(config.getMiniMessagePrefix()).init();
        new InventoryAPI(this).init();
        shopManager = new ShopManager();
        categoryManager = new CategoryManager();
        sendStartupMessage();
        registerCommands();
        loadTestEnvironment();

    }

    private void loadTestEnvironment() {
        categoryManager.registerCategory(new CategoryBuilder().setItemList(List.of(
                        new ShopItemBuilder()
                                .withItemStack(new ItemStack(Material.ACACIA_PLANKS))
                                .buyPrice(200)
                                .sellPrice(100)
                                .build(),
                        new ShopItemBuilder()
                                .withItemStack(new ItemStack(Material.OAK_BOAT))
                                .buyPrice(20)
                                .sellPrice(10)
                                .build()))
                .internalName("holz")
                .displayName(Component.text("Holz", NamedTextColor.RED))
                .description("Stuff.")
                .displayMaterial(Material.OAK_PLANKS)
                .build());
        categoryManager.registerCategory(new CategoryBuilder().setItemList(List.of(
                        new ShopItemBuilder()
                                .withItemStack(new ItemStack(Material.FISHING_ROD))
                                .buyPrice(200)
                                .sellPrice(100)
                                .build(),
                        new ShopItemBuilder()
                                .withItemStack(new ItemStack(Material.COOKED_COD))
                                .buyPrice(20)
                                .build()))
                .internalName("fisch")
                .displayName(Component.text("Fisch", NamedTextColor.RED))
                .description("Stuff.")
                .displayMaterial(Material.FISHING_ROD)
                .build());
        var shop = new CategorizedShop("testshop", Component.text("TEST", NamedTextColor.RED));
        shop.addCategory(categoryManager.getCategories().get("holz"));
        shop.addCategory(categoryManager.getCategories().get("fisch"));
        shopManager.registerShop(shop);

    }

    private void registerCommands() {
        var paperManager = new PaperCommandManager(this);
        paperManager.getLocales().setDefaultLocale(Locale.GERMAN);
        new ShopSystemCommand(this, paperManager);
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
        return Messages.get().deserialize(MessageFormat.format(config.getPrimaryStyle(), s));
    }

    @Override
    public void onDisable() {
        instance = null;
        getSLF4JLogger().info("goodbye!!");
    }

    public EconomyHandler getEconomyHandler() {
        return economyHandler;
    }

    public void sendMessage(CommandSender commandSender, String miniMessage, TagResolver... resolvers) {
        commandSender.sendMessage(getMiniMessage().deserialize(miniMessage, resolvers));
    }


    @NotNull
    public ShopSystemPluginConfig getShopConfig() {
        return config;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public MiniMessage getMiniMessage() {
        return Messages.get().getMiniMessage();
    }

    public CategoryManager getCategoryManager() {
        return categoryManager;
    }
}
