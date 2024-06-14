package it.einjojo.shopsystem;

import it.einjojo.shopsystem.category.Category;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Shop {
    private final List<Category> categories;

    public Shop(@NotNull List<Category> categories) {
        this.categories = categories;
    }
}
