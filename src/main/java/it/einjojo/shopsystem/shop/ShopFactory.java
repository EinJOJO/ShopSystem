package it.einjojo.shopsystem.shop;

import java.util.List;

public class ShopFactory {

    public Shop createShop(String id, ShopType shopType) {
        if (shopType == ShopType.CATEGORIZED) {
            return new CategorizedShop(id);
        } else if (shopType == ShopType.ITEM) {
            // return new ItemShop();
        }
        return null;
    }

    public enum ShopType {
        CATEGORIZED,
        ITEM
    }
}
