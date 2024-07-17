package it.einjojo.shopsystem.gui;

import it.einjojo.shopsystem.category.Category;
import it.einjojo.shopsystem.item.ShopItem;
import it.einjojo.shopsystem.shop.CategorizedShop;
import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

public class CategorizedShopGui extends ShopSystemGui {
    private static final int CATEGORIES_OFFSET = 4 * 9 + 1;
    private static final Icon BACKGROUND = new Icon(Material.BLACK_STAINED_GLASS_PANE).setName("§7");
    private final PaginationManager paginationManager = new PaginationManager(this);
    private final CategorizedShop shop;
    private final ShopItemIconFactory shopItemIconFactory;
    private @NotNull Category selectedCategory;


    public CategorizedShopGui(@NotNull Player player, CategorizedShop shop, ShopItemIconFactory shopItemIconFactory) {
        super(player, "shop", shop.getShopGuiTitle(), 6);
        if (shop.getCategories().isEmpty()) {
            throw new IllegalArgumentException("Shop must have at least one category");
        }
        paginationManager.registerPageSlotsBetween(0, 9 * 3 - 1); // first three rows are for shop items
        selectedCategory = shop.getCategories().get(0);
        this.shop = shop;
        this.shopItemIconFactory = shopItemIconFactory;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        paginationManager.getItems().clear();
        fillGui(BACKGROUND);
        for (ShopItem shopItem : selectedCategory) {
            var icon = shopItemIconFactory.createIcon(shopItem);
            if (icon != null) {
                paginationManager.addItem(icon);
            }
        }
        paginationManager.update();
    }

    public void renderCategories() {
        int categorySlot = 0;
        for (Category category : shop.getCategories()) {
            var icon = new Icon(category.getDisplayMaterial())
                    .toComp()
                    .setName(category.getDisplayName())
                    .onClick((clickEvent) -> {
                        setSelectedCategory(category);
                    });
            if (category.equals(selectedCategory)) {
                icon.enchant(Enchantment.ARROW_INFINITE);
                icon.hideFlags(ItemFlag.HIDE_ENCHANTS);
            }
            addItem(CATEGORIES_OFFSET + (++categorySlot), icon.toIcon());

        }
    }


    public void setSelectedCategory(@NotNull Category selectedCategory) {
        this.selectedCategory = selectedCategory;
        renderCategories();
    }
}
