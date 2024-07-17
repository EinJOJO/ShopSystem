package it.einjojo.shopsystem.category;

import it.einjojo.shopsystem.item.ShopItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CategoryBuilder {
    private String name;
    private @NotNull List<ShopItem> itemList = new LinkedList<>();
    private Component displayName;
    private String description;
    private Material displayMaterial;

    public CategoryBuilder() {
        defaults();
    }

    public CategoryBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CategoryBuilder addItems(Collection<ShopItem> itemList) {
        this.itemList.addAll(itemList);
        return this;
    }

    public CategoryBuilder addItem(ShopItem item) {
        this.itemList.add(item);
        return this;
    }

    public CategoryBuilder removeItem(ShopItem item) {
        this.itemList.remove(item);
        return this;
    }


    public CategoryBuilder displayName(Component displayName) {
        this.displayName = displayName;
        return this;
    }

    public CategoryBuilder description(String description) {
        this.description = description;
        return this;
    }

    public CategoryBuilder displayMaterial(Material displayMaterial) {
        this.displayMaterial = displayMaterial;
        return this;
    }


    public CategoryBuilder setItemList(List<ShopItem> itemList) {
        this.itemList = itemList;
        return this;
    }

    public @Nullable String getName() {
        return name;
    }

    public @NotNull List<ShopItem> getItemList() {
        return itemList;
    }

    public @Nullable Component getDisplayName() {
        return displayName;
    }

    public @Nullable String getDescription() {
        return description;
    }

    public @Nullable Material getDisplayMaterial() {
        return displayMaterial;
    }


    public CategoryBuilder defaults() {
        return this
                .displayName(Component.text("no name", NamedTextColor.WHITE))
                .description("no description")
                .displayMaterial(Material.BARRIER);

    }

    public boolean isComplete() {
        return name != null;
    }


    public Category build() {
        return new Category(name, itemList, displayName, description, displayMaterial);
    }
}