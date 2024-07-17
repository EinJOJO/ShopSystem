package it.einjojo.shopsystem.category;

import com.google.common.base.Preconditions;
import it.einjojo.shopsystem.item.ShopItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * A category is a collection of items that can be bought and sold in a shop.
 */
public class Category implements Iterable<ShopItem> {
    private final @NotNull String name;
    private final @NotNull Component displayName;
    private final @NotNull String description;
    private final @NotNull Material displayMaterial;
    private final @NotNull List<ShopItem> items = new LinkedList<>();


    public Category(@NotNull String name, @Nullable Collection<ShopItem> itemList, @NotNull Component displayName, @NotNull String description, @NotNull Material displayMaterial) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(displayName);
        Preconditions.checkNotNull(description);
        Preconditions.checkNotNull(displayMaterial);
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.displayMaterial = displayMaterial;
        if (itemList != null) {
            this.items.addAll(itemList);
        }
    }

    public @NotNull Material getDisplayMaterial() {
        return displayMaterial;
    }


    public CategoryBuilder builder() {
        return new CategoryBuilder()
                .name(name)
                .setItemList(items)
                .displayName(displayName)
                .description(description)
                .displayMaterial(displayMaterial);
    }

    /**
     * @return Immutable list of items in this category
     */
    public List<ShopItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public boolean addItem(@NotNull ShopItem item) {
        return items.add(item);
    }

    public boolean removeItem(@NotNull ShopItem item) {
        return items.remove(item);
    }

    public @NotNull String getName() {
        return name;
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

    public @NotNull Component getDisplayName() {
        return displayName;
    }

    public @NotNull String getDescription() {
        return description;
    }
}
