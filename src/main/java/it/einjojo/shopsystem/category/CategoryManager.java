package it.einjojo.shopsystem.category;

import java.util.HashMap;
import java.util.Map;

public class CategoryManager {
    private final Map<String, Category> categories = new HashMap<>();

    public Map<String, Category> getCategories() {
        return categories;
    }

    public void registerCategory(Category category) {
        categories.put(category.getName(), category);
    }


}
