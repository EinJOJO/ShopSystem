package it.einjojo.shopsystem.economy;

public class EconomyHandlerFactory {

    public EconomyHandler createEconomyHandler() {
        if (AkaniEconomyHandler.isAvailable()) {
            return new AkaniEconomyHandler();
        } else {
            return new NullEconomyHandler();
        }
    }

}
