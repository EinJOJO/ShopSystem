package it.einjojo.shopsystem.shop;

import it.einjojo.shopsystem.item.ShopItem;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class VariableItemPositionShop implements Shop {
    private final String id;
    private final Component shopName;
    private final Map<Integer, ShopItem> itemMap = new HashMap<>();
    private ShopObserver observer;

    public VariableItemPositionShop(String id, Component shopName) {
        this.id = id;
        this.shopName = shopName;
    }

    @Override
    public String getId() {
        return id;
    }


    public boolean addItem(ShopItem shopItem) {
        int i = 0;
        while (itemMap.containsKey(i)) {
            i++;
        }
        itemMap.put(i, shopItem);
        return true;
    }

    /**
     * @param slot     slot of the item
     * @param shopItem item to add
     * @return false if the slat is occupied
     */
    public boolean addItem(int slot, ShopItem shopItem) {
        if (itemMap.containsKey(slot)) {
            return false;
        }
        itemMap.put(slot, shopItem);
        return true;
    }

    /**
     * @param slot remove item from slot
     * @return whether it could be moved.
     */
    public boolean removeItem(int slot) {
        return (itemMap.remove(slot) != null);
    }

    /**
     * @param oldSlot from
     * @param newSlot to
     * @return true if slot were moved or false if slot to be moved is occupied or no item can be moved.
     */
    public boolean move(int oldSlot, int newSlot) {
        ShopItem toMove = itemMap.get(oldSlot);
        if (toMove == null) return false;
        return addItem(newSlot, toMove);
    }

    @Override
    public Component getShopGuiTitle() {
        return shopName;
    }

    @Override
    public void openBuy(Player player) {

    }

    @Override
    public void openSell(Player player) {

    }

    @Override
    public @Nullable ShopObserver getObserver() {
        return observer;
    }

    @Override
    public void setObserver(@Nullable ShopObserver observer) {
        this.observer = observer;
    }
}
