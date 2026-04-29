package com.skaly.fashion_backend.product.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(UUID.randomUUID())
                .name("Test Product")
                .sku("TEST-001")
                .slug("test-product")
                .basePrice(new BigDecimal("100.00"))
                .isActive(true)
                .isFeatured(false)
                .build();
    }

    @Test
    void addVariant_shouldAddVariantToSet() {
        ProductVariant variant = ProductVariant.builder()
                .id(UUID.randomUUID())
                .size("M")
                .color("Red")
                .stockQuantity(10)
                .build();

        product.addVariant(variant);

        assertEquals(1, product.getVariants().size());
        assertTrue(product.getVariants().contains(variant));
    }

    @Test
    void addImage_shouldAddImageToSet() {
        ProductImage image = ProductImage.builder()
                .id(UUID.randomUUID())
                .url("http://example.com/image.jpg")
                .isPrimary(true)
                .sortOrder(1)
                .build();

        product.addImage(image);

        assertEquals(1, product.getImages().size());
        assertTrue(product.getImages().contains(image));
    }

    @Test
    void builder_shouldSetDefaultValues() {
        Product newProduct = Product.builder()
                .name("New Product")
                .build();

        assertTrue(newProduct.getIsActive());
        assertFalse(newProduct.getIsFeatured());
        assertEquals(0L, newProduct.getViewCount());
        assertEquals(0L, newProduct.getSoldCount());
        assertEquals(BigDecimal.ZERO, newProduct.getRatingAvg());
        assertNotNull(newProduct.getVariants());
        assertNotNull(newProduct.getImages());
        assertNotNull(newProduct.getTags());
    }

    @Test
    void setRatingAvg_shouldUpdateRating() {
        BigDecimal newRating = new BigDecimal("4.5");
        product.setRatingAvg(newRating);

        assertEquals(new BigDecimal("4.5"), product.getRatingAvg());
    }

    @Test
    void incrementViewCount_shouldIncreaseViewCount() {
        long initialCount = product.getViewCount();
        product.setViewCount(initialCount + 1);

        assertEquals(initialCount + 1, product.getViewCount());
    }
}