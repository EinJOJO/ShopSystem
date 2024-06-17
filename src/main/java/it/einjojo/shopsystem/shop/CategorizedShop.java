package it.einjojo.shopsystem.shop;

import it.einjojo.shopsystem.category.Category;
import it.einjojo.shopsystem.gui.CategorizedShopGui;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class CategorizedShop implements Shop {
    private final List<Category> categories = new LinkedList<>();
    private final String id;

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

    @Override
    public void open(Player player) {
        new CategorizedShopGui(player, this).open();
    }


    public void addCategory(Category category) {
        categories.add(category);
    }

    public List<Category> getCategories() {
        return categories;
    }

}
