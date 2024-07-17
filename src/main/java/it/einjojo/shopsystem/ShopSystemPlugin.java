package it.einjojo.shopsystem;

import co.aikar.commands.PaperCommandManager;
import it.einjojo.shopsystem.category.CategoryBuilder;
import it.einjojo.shopsystem.category.CategoryManager;
import it.einjojo.shopsystem.command.ShopSystemCommand;
import it.einjojo.shopsystem.config.ShopSystemPluginConfig;
import it.einjojo.shopsystem.item.handler.ItemStackTradeHandler;
import it.einjojo.shopsystem.item.ShopItemBuilder;
import it.einjojo.shopsystem.shop.CategorizedShop;
import it.einjojo.shopsystem.shop.ShopManager;
import it.einjojo.shopsystem.util.Messages;
import mc.obliviate.inventory.InventoryAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
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

    public static ShopSystemPlugin getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Plugin not enabled yet");
        }
        return instance;
    }

    @Override
    public void onEnable() {
        config = new ShopSystemPluginConfig(this);
        new Messages(config.getMiniMessagePrefix()).init();
        new InventoryAPI(this).init();
        instance = this;
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
                                .build()))
                .name("holz")
                .displayName(Component.text("Holz", NamedTextColor.RED))
                .description("")
                .displayMaterial(Material.OAK_PLANKS)
                .build());
        var shop = new CategorizedShop("testshop");
        shop.addCategory(categoryManager.getCategories().get("test"));
        shopManager.registerShop(shop);

    }

    private void registerCommands() {
        var paperManager = new PaperCommandManager(this);
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
