package com.example.shoppingcart;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartTest {

    @Test
    void shouldAddItemToCart() {
        ShoppingCart cart = new ShoppingCart();

        cart.addItem("Headphones", 300.00, 1);

        assertEquals(1, cart.getItemsCount());
    }

    @Test
    void shouldDeleteItemFromCart() {
        ShoppingCart cart = new ShoppingCart();

        cart.addItem("Headphones", 300.00, 1);
        cart.addItem("Microphone", 500.00, 1);

        cart.deleteItem("Headphones");

        assertEquals(1, cart.getItemsCount());
        assertFalse(cart.containsItem("Headphones"));
    }

    @Test
    void shouldCalculateTotalPrice() {
        ShoppingCart cart = new ShoppingCart();

        cart.addItem("Headphones", 300.00, 1);
        cart.addItem("Microphone", 500.00, 1);

        assertEquals(800.00, cart.getTotalPrice());
    }

    @Test
    void shouldApplyDiscount() {
        ShoppingCart cart = new ShoppingCart();

        cart.addItem("Headphones", 300.00, 1);
        cart.addItem("Microphone", 500.00, 1);

        cart.applyDiscount(50);

        assertEquals(400.00, cart.getTotalPrice());
    }

    @Test
    void shouldUpdateNumberOfGivenItem() {
        ShoppingCart cart = new ShoppingCart();

        cart.addItem("Headphones", 300.00, 1);
        cart.addItem("Headphones", 300.00, 1);

        assertEquals(2, cart.getItemQuantity("Headphones"));
        assertEquals(600.00, cart.getTotalPrice());
    }
}