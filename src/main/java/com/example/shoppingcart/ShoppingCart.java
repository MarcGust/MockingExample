package com.example.shoppingcart;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<String> items = new ArrayList<>();

    public void addItem(String itemName, double price) {
        items.add(itemName);
    }

    public int getItemsCount() {
        return items.size();
    }

    public void deleteItem(String itemName) {
        items.remove(itemName);
    }

    public boolean containsItem(String itemName) {
        return items.contains(itemName);
    }
}

