package com.example.shoppingcart;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {
    private Map<String, Double> items;

    public ShoppingCart() {
        items = new HashMap<>();
    }

    public void addItem(String itemName, double price) {
        items.put(itemName, price);
    }

    public int getItemsCount() {
        return items.size();
    }

    public void deleteItem(String itemName) {
        items.remove(itemName);
    }

    public boolean containsItem(String itemName) {
        return items.containsKey(itemName);
    }

    public double getTotalPrice() {
        return items.values().stream().mapToDouble(Double::doubleValue).sum();
    }
}

