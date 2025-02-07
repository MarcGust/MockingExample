package com.example.shoppingcart;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {

    private Map<String, Item> items;
    private double discount;

    public ShoppingCart() {
        items = new HashMap<>();
        discount = 0.0;
    }

    public void addItem(String itemName, double price, int quantity) {
        if (itemName == null || itemName.isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be null or empty");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        if (items.containsKey(itemName)) {
            Item item = items.get(itemName);
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            items.put(itemName, new Item(itemName, price, quantity));
        }
    }

    public void deleteItem(String itemName) {
        items.remove(itemName);
    }

    public boolean containsItem(String itemName) {
        return items.containsKey(itemName);
    }

    public int getItemsCount() {
        return items.size();
    }

    public double getTotalPrice() {
        double total = items.values().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        return total - (total * discount / 100);
    }

    public void applyDiscount(double discountPercentage) {
        this.discount = discountPercentage;
    }

    public int getItemQuantity(String itemName) {
        return items.containsKey(itemName) ? items.get(itemName).getQuantity() : 0;
    }


}