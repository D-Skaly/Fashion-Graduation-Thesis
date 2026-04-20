package com.skaly.fashion_backend.product.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaProductImageRepository extends JpaRepository<ProductImageEntity, UUID> {

    List<ProductImageEntity> findByProductIdOrderBySortOrderAsc(UUID productId);

    Optional<ProductImageEntity> findByProductIdAndIsPrimaryTrue(UUID productId);

    @Modifying
    @Query("UPDATE ProductImageEntity pi SET pi.isPrimary = false WHERE pi.product.id = :productId")
    void clearPrimaryImage(@Param("productId") UUID productId);

    @Query("SELECT pi FROM ProductImageEntity pi WHERE pi.product.id = :productId AND pi.isPrimary = true")
    Optional<ProductImageEntity> findPrimaryImageByProductId(@Param("productId") UUID productId);

    long countByProductId(UUID productId);

    void deleteByProductId(UUID productId);
}
