package it.einjojo.shopsystem.shop;

import com.google.common.base.Preconditions;
import it.einjojo.shopsystem.ShopSystemPlugin;
import it.einjojo.shopsystem.category.Category;
import it.einjojo.shopsystem.gui.CategorizedShopGui;
import it.einjojo.shopsystem.gui.ShopItemIconFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class CategorizedShop implements Shop {
    private final List<Category> categories = new LinkedList<>();
    private final String id;
    private @Nullable ShopChangeObserver observer;


    @Override
    public void openBuy(Player player) {
        new CategorizedShopGui(player, this, ShopItemIconFactory.BUY_ONLY, ShopSystemPlugin.getInstance()).open();
    }

    @Override
    public void openSell(Player player) {
        new CategorizedShopGui(player, this, ShopItemIconFactory.SELL_ONLY, ShopSystemPlugin.getInstance()).open();
    }

    public void addCategory(@NotNull Category category) {
        Preconditions.checkNotNull(category, "Category must not be null when added");
        if (categories.contains(category)) {
            return;
        }
        categories.add(category);
    }

    public List<Category> getCategories() {
        return categories;
    }


    @Override
    public @Nullable ShopChangeObserver getObserver() {
        return observer;
    }

    @Override
    public void setObserver(@Nullable ShopChangeObserver observer) {
        this.observer = observer;
    }

    public CategorizedShop(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Component getShopGuiTitle() {
        return Component.text("Shop");
    }


}
