package it.einjojo.shopsystem.gui;

import it.einjojo.shopsystem.ShopSystemPlugin;
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
    private static final Icon BACKGROUND = new Icon(Material.BLACK_STAINED_GLASS_PANE).setName("§7");
    private final PaginationManager itemsPagination = new PaginationManager(this);
    private final PaginationManager categoriesPagination = new PaginationManager(this);
    private final CategorizedShop shop;
    private final ShopItemIconFactory shopItemIconFactory;
    private final ShopSystemPlugin plugin;
    private @NotNull Category selectedCategory;


    public CategorizedShopGui(@NotNull Player player, CategorizedShop shop, ShopItemIconFactory shopItemIconFactory, ShopSystemPlugin plugin) {
        super(player, "shop", shop.getShopGuiTitle(), 6);
        this.plugin = plugin;
        if (shop.getCategories().isEmpty()) {
            player.sendMessage("§cDer Shop besitzt keine Kateogrien!");
            throw new IllegalArgumentException("Shop has no categories.");
        }
        categoriesPagination.registerPageSlotsBetween(9 * 4 + 1, 9 * 4 + 7);
        itemsPagination.registerPageSlotsBetween(0, 9 * 3 - 1);
        selectedCategory = shop.getCategories().get(0);
        this.shop = shop;
        this.shopItemIconFactory = shopItemIconFactory;
    }

    private void addPageSwitcher() {
        var prevItems = new Icon(Material.ARROW)
                .setName("§7Vorherige Items")
                .onClick((clickEvent) -> {
                    itemsPagination.goPreviousPage();
                });
        var nextItems = new Icon(Material.ARROW)
                .setName("§7Nächsten Items")
                .onClick((clickEvent) -> {
                    itemsPagination.goNextPage();
                });
        addItem(9 * 3 + 3, prevItems);
        addItem(9 * 3 + 5, nextItems);

        var prevCategories = new Icon(Material.ARROW)
                .setName("§7Vorherige Seite")
                .onClick((clickEvent) -> {
                    categoriesPagination.goPreviousPage();
                });
        var nextCategories = new Icon(Material.ARROW)
                .setName("§7Nächste Seite")
                .onClick((clickEvent) -> {
                    categoriesPagination.goNextPage();
                });

        addItem(9 * 4, prevCategories);
        addItem(9 * 5 - 1, nextCategories);

    }


    @Override
    public void onOpen(InventoryOpenEvent event) {
        fillGui(BACKGROUND);
        addPageSwitcher();
        renderItems();
        renderCategories();
    }

    public void renderItems() {
        itemsPagination.getItems().clear();
        for (ShopItem shopItem : selectedCategory) {
            var icon = shopItemIconFactory.createIcon(shopItem, plugin);
            if (icon != null) {
                itemsPagination.addItem(icon);
            }
        }
        itemsPagination.update();
    }


    public void renderCategories() {
        categoriesPagination.getItems().clear();
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
            categoriesPagination.addItem(icon.toIcon());
        }
        categoriesPagination.update();
    }


    public void setSelectedCategory(@NotNull Category selectedCategory) {
        this.selectedCategory = selectedCategory;
        open();
    }
}
