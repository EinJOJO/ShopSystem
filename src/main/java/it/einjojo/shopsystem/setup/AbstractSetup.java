package it.einjojo.shopsystem.setup;

import it.einjojo.shopsystem.ShopSystemPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Abstract class for setup classes.
 * Registers the event listener and provides a method to unregister it.
 *
 * @param <T> The type of the setup object
 */
public abstract class AbstractSetup<T> implements Listener {
    public static final Map<UUID, AbstractSetup<?>> SETUP_MAP = new HashMap<>();
    private final ShopSystemPlugin plugin;
    protected @Nullable Consumer<T> onComplete;
    private final UUID playerUuid;
    protected final Player player;


    protected AbstractSetup(ShopSystemPlugin plugin, Player player) {
        this.plugin = plugin;
        this.playerUuid = player.getUniqueId();
        this.player = player;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (event.getPlayer().getUniqueId().equals(playerUuid)) {
            unregister();
        }
    }

    public void register() {
        AbstractSetup<?> oldSetup = SETUP_MAP.put(getPlayerUuid(), this);
        if (oldSetup != null) {
            oldSetup.unregister();
        }
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
        postRegister();
    }

    protected abstract void postRegister();

    protected abstract void complete();


    public void setOnComplete(@Nullable Consumer<T> onComplete) {
        this.onComplete = onComplete;
    }

    protected void callCompletionConsumer(T object) {
        if (onComplete != null) {
            onComplete.accept(object);
        }
    }


    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
        SETUP_MAP.remove(playerUuid);
    }


    public ShopSystemPlugin getPlugin() {
        return plugin;
    }

}
