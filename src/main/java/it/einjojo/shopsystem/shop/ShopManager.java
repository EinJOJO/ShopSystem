package it.einjojo.shopsystem.shop;

import java.util.HashMap;
import java.util.Map;

public class ShopManager {
    private final Map<String, Shop> shops = new HashMap<>();

    public Map<String, Shop> getShops() {
        return shops;
    }

    public void registerShop(Shop shop) {
        shops.put(shop.getId(), shop);
    }
}
