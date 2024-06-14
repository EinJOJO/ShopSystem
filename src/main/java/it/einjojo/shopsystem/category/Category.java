package it.einjojo.shopsystem.category;

import it.einjojo.shopsystem.item.ShopItem;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class Category {
    private final List<ShopItem> itemList;
    @Nullable
    private transient CategoryChangeObserver observer;

    public Category(List<ShopItem> itemList) {
        this.itemList = itemList;
    }

    /**
     * @return Immutable list of items in this category
     */
    public List<ShopItem> getItemList() {
        return Collections.unmodifiableList(itemList);
    }

    public void addItem(ShopItem item) {
        itemList.add(item);
        callChangeObserver();
    }

    public void removeItem(ShopItem item) {
        itemList.remove(item);
        callChangeObserver();
    }

    protected void callChangeObserver() {
        if (observer != null) {
            observer.onCategoryChange(this);
        }
    }

    public @Nullable CategoryChangeObserver getObserver() {
        return observer;
    }

    public void setObserver(@Nullable CategoryChangeObserver observer) {
        this.observer = observer;
    }
}
