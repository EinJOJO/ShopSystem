package it.einjojo.shopsystem.category;

import it.einjojo.shopsystem.item.ShopItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * A category is a collection of items that can be bought and sold in a shop.
 */
public class Category implements Iterable<ShopItem> {
    private final String name;
    private final Material displayMaterial;
    private final List<ShopItem> items = new LinkedList<>();
    @Nullable
    private transient CategoryChangeObserver observer;


    public Category(String name, List<ShopItem> itemList, Material displayMaterial) {
        this.name = name;
        this.displayMaterial = displayMaterial;
        this.items.addAll(itemList);
    }

    public Material getDisplayMaterial() {
        return displayMaterial;
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

    @NotNull
    @Override
    public Iterator<ShopItem> iterator() {
        return getItems().iterator();
    }

    @Override
    public void forEach(Consumer<? super ShopItem> action) {
        getItems().forEach(action);
    }

    @Override
    public Spliterator<ShopItem> spliterator() {
        return getItems().spliterator();
    }
}
