package it.einjojo.shopsystem.economy;

import java.util.UUID;

public interface EconomyHandler {

    boolean has(UUID player, double amount);

    void remove(UUID player, double amount);

    void add(UUID player, double amount);

}
