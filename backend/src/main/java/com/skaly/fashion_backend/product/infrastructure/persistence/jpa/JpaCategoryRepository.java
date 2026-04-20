package com.skaly.fashion_backend.product.infrastructure.persistence.jpa;
import com.skaly.fashion_backend.product.CategoryEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, UUID> {
}
