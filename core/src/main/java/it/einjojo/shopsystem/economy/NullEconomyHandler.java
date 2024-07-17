package it.einjojo.shopsystem.economy;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

public class NullEconomyHandler implements EconomyHandler {
    @Override
    public boolean has(UUID player, int amount) {
        Player p = Bukkit.getPlayer(player);
        if (p == null) {
            return false;
        }
        p.sendActionBar(Component.text("Economy Check: Halte ein Item in der Hand den Check zu bestehen.", NamedTextColor.GRAY));
        return !p.getInventory().getItemInMainHand().getType().isAir();
    }

    @Override
    public void remove(UUID player, int amount) {
        withPlayer(player, p -> {
            p.sendMessage("§7Test-Transaction: §c-" + amount);
        });
    }

    @Override
    public void add(UUID player, int amount) {
        withPlayer(player, p -> {
            p.sendMessage("§7Test-Transaction: §a+" + amount);
        });
    }

    private void withPlayer(UUID uuid, Consumer<Player> consumer) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            consumer.accept(player);
        }
    }
}
