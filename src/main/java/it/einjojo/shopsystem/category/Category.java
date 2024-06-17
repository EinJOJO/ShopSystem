package it.einjojo.shopsystem.category;

import it.einjojo.shopsystem.item.ShopItem;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Category {
    private final String name;
    private final List<ShopItem> items = new LinkedList<>();
    @Nullable
    private transient CategoryChangeObserver observer;


    public Category(String name, List<ShopItem> itemList) {
        this.name = name;
        this.items.addAll(itemList);
    }

    /**
     * @return Immutable list of items in this category
     */
    public List<ShopItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(ShopItem item) {
        items.add(item);
        callChangeObserver();
    }

    public void removeItem(ShopItem item) {
        items.remove(item);
        callChangeObserver();
    }

    protected void callChangeObserver() {
        if (observer != null) {
            observer.onCategoryChange(this);
        }
    }

    public String getName() {
        return name;
    }

    public @Nullable CategoryChangeObserver getObserver() {
        return observer;
    }

    public void setObserver(@Nullable CategoryChangeObserver observer) {
        this.observer = observer;
    }
}
