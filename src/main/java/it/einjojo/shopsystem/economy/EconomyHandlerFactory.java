package it.einjojo.shopsystem.economy;

public class EconomyHandlerFactory {

    EconomyHandler createEconomyHandler() {
        return new AkaniEconomyHandler();
    }

}
