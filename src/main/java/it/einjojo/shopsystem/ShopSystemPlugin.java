package it.einjojo.shopsystem;

import it.einjojo.shopsystem.config.ShopSystemPluginConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.MessageFormat;

public class ShopSystemPlugin extends JavaPlugin {
    private static volatile ShopSystemPlugin instance;
    private ShopSystemPluginConfig config;
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
        config = new ShopSystemPluginConfig(this);
        miniMessage = createMiniMessage(MiniMessage.miniMessage().deserialize(config.getMiniMessagePrefix()));
        sendStartupMessage();

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
        c.sendMessage(deserializeWithPrimaryStyle("<gray>ShopSystem</gray> v" + getDescription().getVersion() + "<green> enabled.</green> <gray>- Created by </gray>EinJoJo"));

    }

    private Component deserializeWithPrimaryStyle(String s) {
        return miniMessage.deserialize(MessageFormat.format(config.getPrimaryStyle(), s));
    }

    @Override
    public void onDisable() {

    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }
}
