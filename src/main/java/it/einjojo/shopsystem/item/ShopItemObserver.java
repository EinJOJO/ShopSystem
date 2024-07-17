package it.einjojo.shopsystem.item;

public interface ShopItemObserver {

    void onChange(ShopItem item);

    void onSell(ShopItem item);

    void onBuy(ShopItem item);

    void onStockChange(ShopItem item);

    void onBuyPriceChange(ShopItem item);
    void onSellPriceChange(ShopItem item);

    void onConditionChange(ShopItem item);


}
