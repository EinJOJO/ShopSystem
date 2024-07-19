package it.einjojo.shopsystem.shop;


import net.kyori.adventure.text.Component;

public class ShopFactory {

    public Shop createShop(String id, Component shopName, ShopType shopType) {
        if (shopType == ShopType.CATEGORIZED) {
            return new CategorizedShop(id, shopName);
        } else if (shopType == ShopType.VARIABLE_ITEM_POSITION_SHOP) {
            return new VariableItemPositionShop(id, shopName);
        }
        return null;
    }

    public enum ShopType {
        CATEGORIZED,
        VARIABLE_ITEM_POSITION_SHOP
    }
}
