package it.einjojo.shopsystem.economy;

import java.util.UUID;

public interface EconomyHandler {

    boolean has(UUID player, int amount);

    void remove(UUID player, int amount);

    void add(UUID player, int amount);

}
