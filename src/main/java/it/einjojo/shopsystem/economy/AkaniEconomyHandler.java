package it.einjojo.shopsystem.economy;

import java.util.UUID;

public class AkaniEconomyHandler implements EconomyHandler {


    @Override
    public boolean has(UUID player, double amount) {
        return false;
    }

    @Override
    public void remove(UUID player, double amount) {

    }

    @Override
    public void add(UUID player, double amount) {

    }
}
