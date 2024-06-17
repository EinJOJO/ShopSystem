package it.einjojo.shopsystem.gui;

import it.einjojo.shopsystem.category.Category;
import it.einjojo.shopsystem.item.ShopItem;
import it.einjojo.shopsystem.shop.CategorizedShop;
import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

public class CategorizedShopGui extends ShopSystemGui {
    private final PaginationManager paginationManager = new PaginationManager(this);
    private @NotNull Category selectedCategory;

    public CategorizedShopGui(@NotNull Player player, CategorizedShop shop) {
        super(player, "shop", shop.getShopGuiTitle(), 6);
        if (shop.getCategories().isEmpty()) {
            throw new IllegalArgumentException("Shop must have at least one category");
        }
        paginationManager.registerPageSlotsBetween(0, 9 * 3 - 1);
        selectedCategory = shop.getCategories().get(0);

    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        paginationManager.getItems().clear();
        for (ShopItem shopItem : selectedCategory.getItems()) {
            paginationManager.addItem(new Icon(shopItem.getDisplayItem()).onClick((clickEvent) -> onShopItemClick(shopItem, clickEvent)));
        }
        paginationManager.update();
    }

    public void onShopItemClick(ShopItem clickedShopItem, InventoryClickEvent event) {
        playClickSound();
    }

    public void setSelectedCategory(Category selectedCategory) {
        this.selectedCategory = selectedCategory;
    }
}
