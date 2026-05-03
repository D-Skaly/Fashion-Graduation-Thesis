package com.skaly.fashion_backend.product.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductImageTest {

    @Test
    void builder_shouldSetDefaultValues() {
        ProductImage image = ProductImage.builder()
                .url("http://example.com/image.jpg")
                .build();

        assertNotNull(image.getId());
        assertEquals("http://example.com/image.jpg", image.getUrl());
        assertNull(image.getAltText());
        assertNull(image.getIsPrimary());
        assertNull(image.getDisplayOrder());
    }

    @Test
    void builder_shouldSetAltText() {
        ProductImage image = ProductImage.builder()
                .url("http://example.com/image.jpg")
                .altText("A nice shirt")
                .build();

        assertEquals("A nice shirt", image.getAltText());
    }

    @Test
    void builder_shouldSetIsPrimary() {
        ProductImage image = ProductImage.builder()
                .url("http://example.com/image.jpg")
                .isPrimary(true)
                .build();

        assertTrue(image.getIsPrimary());
    }

    @Test
    void builder_shouldSetDisplayOrder() {
        ProductImage image = ProductImage.builder()
                .url("http://example.com/image.jpg")
                .displayOrder(5)
                .build();

        assertEquals(Integer.valueOf(5), image.getDisplayOrder());
    }

    @Test
    void updateUrl_shouldUpdateUrl() {
        ProductImage image = ProductImage.builder()
                .url("http://example.com/old.jpg")
                .build();

        image.setUrl("http://example.com/new.jpg");
        assertEquals("http://example.com/new.jpg", image.getUrl());
    }

    @Test
    void updateAltText_shouldUpdateAltText() {
        ProductImage image = ProductImage.builder()
                .url("http://example.com/image.jpg")
                .altText("Old alt text")
                .build();

        image.setAltText("New alt text");
        assertEquals("New alt text", image.getAltText());
    }

    @Test
    void updateIsPrimary_shouldUpdatePrimaryStatus() {
        ProductImage image = ProductImage.builder()
                .url("http://example.com/image.jpg")
                .isPrimary(false)
                .build();

        image.setIsPrimary(true);
        assertTrue(image.getIsPrimary());

        image.setIsPrimary(false);
        assertFalse(image.getIsPrimary());
    }

    @Test
    void updateDisplayOrder_shouldUpdateOrder() {
        ProductImage image = ProductImage.builder()
                .url("http://example.com/image.jpg")
                .displayOrder(1)
                .build();

        image.setDisplayOrder(10);
        assertEquals(Integer.valueOf(10), image.getDisplayOrder());
    }
}
