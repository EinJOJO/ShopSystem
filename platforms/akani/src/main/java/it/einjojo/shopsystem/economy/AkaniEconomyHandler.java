package it.einjojo.shopsystem.economy;

import it.einjojo.akani.core.api.AkaniCoreProvider;
import it.einjojo.akani.core.api.economy.EconomyManager;

import java.util.UUID;

public class AkaniEconomyHandler implements EconomyHandler {
    private final EconomyManager economyManager;

    public static boolean isAvailable() {
        try {
            Class.forName("it.einjojo.akani.core.api.AkaniCoreProvider");
            return true;
        } catch (ClassNotFoundException e) {
            return false;

        }
    }


    public AkaniEconomyHandler() {
        this.economyManager = AkaniCoreProvider.get().coinsManager();
    }

    @Override
    public boolean has(UUID player, int amount) {
        var optionalHolder = economyManager.playerEconomy(player);
        return optionalHolder.filter(economyHolder -> economyHolder.balance() >= amount)
                .isPresent();
    }

    @Override
    public void remove(UUID player, int amount) {
        economyManager.playerEconomy(player)
                .ifPresent(economyHolder -> economyHolder.removeBalance(amount));
    }

    @Override
    public void add(UUID player, int amount) {
        economyManager.playerEconomy(player)
                .ifPresent(economyHolder -> economyHolder.addBalance(amount));

    }
}
