package com.example.shoppingcart;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartTest {

    @Test
    void shouldAddItemToCart() {
        ShoppingCart cart = new ShoppingCart();

        cart.addItem("Headphones", 300.00);

        assertEquals(1, cart.getItemsCount());
    }
}