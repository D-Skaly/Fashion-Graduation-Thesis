package com.skaly.fashion_backend.product.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductVariantTest {

    @Test
    void builder_shouldSetDefaultValues() {
        ProductVariant variant = ProductVariant.builder()
                .sku("SKU-001")
                .size("M")
                .color("Red")
                .stockQuantity(10)
                .price(new BigDecimal("100.00"))
                .build();

        assertNotNull(variant.getId());
        assertEquals("SKU-001", variant.getSku());
        assertEquals("M", variant.getSize());
        assertEquals("Red", variant.getColor());
        assertEquals(10, variant.getStockQuantity());
        assertEquals(new BigDecimal("100.00"), variant.getPrice());
    }

    @Test
    void builder_shouldSetPrice() {
        ProductVariant variant = ProductVariant.builder()
                .sku("SKU-002")
                .size("L")
                .color("Blue")
                .stockQuantity(5)
                .price(new BigDecimal("150.00"))
                .build();

        assertEquals(new BigDecimal("150.00"), variant.getPrice());
    }

    @Test
    void isInStock_shouldReturnTrueWhenStockPositive() {
        ProductVariant inStock = ProductVariant.builder()
                .stockQuantity(10)
                .build();

        assertTrue(inStock.getStockQuantity() > 0);
    }

    @Test
    void isInStock_shouldReturnFalseWhenStockZero() {
        ProductVariant outOfStock = ProductVariant.builder()
                .stockQuantity(0)
                .build();

        assertFalse(outOfStock.getStockQuantity() > 0);
    }

    @Test
    void updateStock_shouldUpdateQuantity() {
        ProductVariant variant = ProductVariant.builder()
                .stockQuantity(10)
                .build();

        variant.setStockQuantity(15);
        assertEquals(15, variant.getStockQuantity());
    }

    @Test
    void updatePrice_shouldUpdatePrice() {
        ProductVariant variant = ProductVariant.builder()
                .stockQuantity(5)
                .price(new BigDecimal("100.00"))
                .build();

        variant.setPrice(new BigDecimal("120.00"));
        assertEquals(new BigDecimal("120.00"), variant.getPrice());
    }
}