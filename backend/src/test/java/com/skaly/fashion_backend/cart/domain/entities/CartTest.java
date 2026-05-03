package com.skaly.fashion_backend.cart.domain.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = Cart.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .build();
    }

    @Test
    void addItem_shouldAddNewItem() {
        CartItem item = CartItem.builder()
                .productVariantId(UUID.randomUUID())
                .quantity(2)
                .snapshotPrice(new BigDecimal("50.00"))
                .build();

        cart.addItem(item);

        assertEquals(1, cart.getItems().size());
        assertTrue(cart.getItems().contains(item));
    }

    @Test
    void addItem_shouldUpdateQuantityForExistingProduct() {
        UUID variantId = UUID.randomUUID();
        
        CartItem item1 = CartItem.builder()
                .productVariantId(variantId)
                .quantity(2)
                .snapshotPrice(new BigDecimal("50.00"))
                .build();
        
        CartItem item2 = CartItem.builder()
                .productVariantId(variantId)
                .quantity(3)
                .snapshotPrice(new BigDecimal("50.00"))
                .build();

        cart.addItem(item1);
        cart.addItem(item2);

        assertEquals(1, cart.getItems().size()); // Same variant, should merge
        CartItem existingItem = cart.getItems().iterator().next();
        assertEquals(5, existingItem.getQuantity()); // 2 + 3 = 5
    }

    @Test
    void removeItem_shouldRemoveItem() {
        CartItem item = CartItem.builder()
                .productVariantId(UUID.randomUUID())
                .quantity(1)
                .snapshotPrice(BigDecimal.TEN)
                .build();

        cart.addItem(item);
        assertEquals(1, cart.getItems().size());

        cart.removeItem(item.getId());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void removeItem_shouldNotFailForNonExistentItem() {
        cart.removeItem(UUID.randomUUID()); // Should not throw
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void clearItems_shouldRemoveAllItems() {
        CartItem item1 = CartItem.builder()
                .productVariantId(UUID.randomUUID())
                .quantity(1)
                .snapshotPrice(BigDecimal.TEN)
                .build();

        CartItem item2 = CartItem.builder()
                .productVariantId(UUID.randomUUID())
                .quantity(2)
                .snapshotPrice(BigDecimal.TEN)
                .build();

        cart.addItem(item1);
        cart.addItem(item2);
        assertEquals(2, cart.getItems().size());

        cart.clearItems();
        assertEquals(0, cart.getItems().size());
    }
}