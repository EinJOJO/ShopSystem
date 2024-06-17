package it.einjojo.shopsystem.shop;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public interface Shop {

    String getId();

    Component getShopGuiTitle();

    void open(Player player);

}
