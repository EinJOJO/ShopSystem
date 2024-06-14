package it.einjojo.shopsystem.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopSystemPluginConfig {

    private final FileConfiguration config;

    public ShopSystemPluginConfig(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
    }

    public final String getMiniMessagePrefix() {
        return config.getString("prefix");
    }

    public String getPrimaryStyle() {
        return config.getString("primary-style");
    }

}
