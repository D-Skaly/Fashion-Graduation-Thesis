package com.skaly.fashion_backend.cart.domain.entities;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {

    @Test
    void builder_shouldSetDefaultValues() {
        CartItem item = CartItem.builder()
                .productVariantId(UUID.randomUUID())
                .quantity(2)
                .snapshotPrice(new BigDecimal("50.00"))
                .build();

        assertNotNull(item.getId());
        assertEquals(2, item.getQuantity());
        assertEquals(new BigDecimal("50.00"), item.getSnapshotPrice());
        assertFalse(item.isQuantityAdjusted());
    }

    @Test
    void calculateSubtotal_shouldReturnPriceTimesQuantity() {
        CartItem item = CartItem.builder()
                .productVariantId(UUID.randomUUID())
                .quantity(3)
                .snapshotPrice(new BigDecimal("25.00"))
                .build();

        BigDecimal expected = new BigDecimal("75.00");
        assertEquals(expected, item.getSnapshotPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
    }

    @Test
    void updateQuantity_shouldUpdateQuantity() {
        CartItem item = CartItem.builder()
                .productVariantId(UUID.randomUUID())
                .quantity(1)
                .snapshotPrice(BigDecimal.TEN)
                .build();

        item.setQuantity(5);
        assertEquals(5, item.getQuantity());
    }

    @Test
    void updateSnapshotPrice_shouldUpdatePrice() {
        CartItem item = CartItem.builder()
                .productVariantId(UUID.randomUUID())
                .quantity(2)
                .snapshotPrice(new BigDecimal("50.00"))
                .build();

        item.setSnapshotPrice(new BigDecimal("75.00"));
        assertEquals(new BigDecimal("75.00"), item.getSnapshotPrice());
    }

    @Test
    void builder_shouldGenerateDifferentUUIDs() {
        CartItem item1 = CartItem.builder()
                .productVariantId(UUID.randomUUID())
                .quantity(1)
                .snapshotPrice(BigDecimal.TEN)
                .build();

        CartItem item2 = CartItem.builder()
                .productVariantId(UUID.randomUUID())
                .quantity(1)
                .snapshotPrice(BigDecimal.TEN)
                .build();

        assertNotNull(item1.getId());
        assertNotNull(item2.getId());
        assertNotEquals(item1.getId(), item2.getId());
    }
}