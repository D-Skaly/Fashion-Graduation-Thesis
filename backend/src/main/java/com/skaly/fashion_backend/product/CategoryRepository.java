package com.skaly.fashion_backend.product;
import java.util.Optional;
import java.util.UUID;
public interface CategoryRepository {
    Optional<Category> findCategoryById(UUID id);
    Category save(Category category);
}

