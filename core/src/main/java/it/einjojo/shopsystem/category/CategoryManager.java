package it.einjojo.shopsystem.category;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CategoryManager {
    private final Map<String, Category> categories = new HashMap<>();
    private final List<CategoryListener> listeners = new LinkedList<>();

    public Map<String, Category> getCategories() {
        return categories;
    }

    public void registerCategory(Category category) {
        Category replaced = categories.put(category.getInternalName(), category);
        if (replaced != null) {
            listeners.forEach(listener -> listener.onUpdate(replaced, category));
        }
    }

    public void unregisterCategory(Category category) {
        categories.remove(category.getInternalName());
    }

    public void subscribe(CategoryListener listener) {
        listeners.add(listener);
    }

    public void unsubscribe(CategoryListener listener) {
        listeners.remove(listener);
    }


}
