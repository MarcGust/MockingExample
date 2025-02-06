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
}

