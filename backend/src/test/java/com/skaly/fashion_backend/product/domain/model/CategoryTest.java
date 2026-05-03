package com.skaly.fashion_backend.product.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void builder_shouldSetDefaultValues() {
        Category category = Category.builder()
                .name("Clothing")
                .slug("clothing")
                .build();

        assertNotNull(category.getId());
        assertEquals("Clothing", category.getName());
        assertEquals("clothing", category.getSlug());
        assertNull(category.getDescription());
        assertNull(category.getParentId());
    }

    @Test
    void builder_shouldSetDescription() {
        Category category = Category.builder()
                .name("Tops")
                .slug("tops")
                .description("Upper body clothing")
                .build();

        assertEquals("Upper body clothing", category.getDescription());
    }

    @Test
    void builder_shouldSetParentId() {
        UUID parentId = UUID.randomUUID();
        
        Category subCategory = Category.builder()
                .name("Shirts")
                .slug("shirts")
                .parentId(parentId)
                .build();

        assertEquals(parentId, subCategory.getParentId());
    }

    @Test
    void updateName_shouldUpdateName() {
        Category category = Category.builder()
                .name("Old Name")
                .slug("old-name")
                .build();

        category.setName("New Name");
        assertEquals("New Name", category.getName());
    }

    @Test
    void updateSlug_shouldUpdateSlug() {
        Category category = Category.builder()
                .name("Test")
                .slug("old-slug")
                .build();

        category.setSlug("new-slug");
        assertEquals("new-slug", category.getSlug());
    }

    @Test
    void builder_shouldGenerateDifferentUUIDs() {
        Category category1 = Category.builder()
                .name("Category 1")
                .slug("cat-1")
                .build();

        Category category2 = Category.builder()
                .name("Category 2")
                .slug("cat-2")
                .build();

        assertNotNull(category1.getId());
        assertNotNull(category2.getId());
        assertNotEquals(category1.getId(), category2.getId());
    }
}