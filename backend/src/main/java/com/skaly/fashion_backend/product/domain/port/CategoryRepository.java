package com.skaly.fashion_backend.product.domain.port;

import com.skaly.fashion_backend.product.domain.model.Category;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {
    Optional<Category> findCategoryById(UUID id);
    Category save(Category category);
}

