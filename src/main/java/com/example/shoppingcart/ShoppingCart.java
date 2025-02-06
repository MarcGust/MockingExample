package com.example.shoppingcart;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {
    private Map<String, Double> items;
    private double discount;

    public ShoppingCart() {
        items = new HashMap<>();
        discount = 0.0;
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
        double totalPrice = items.values().stream().mapToDouble(Double::doubleValue).sum();
        return totalPrice - (totalPrice * discount / 100);
    }

    public void applyDiscount(double discountPercentage) {
        this.discount = discountPercentage;
    }
}

