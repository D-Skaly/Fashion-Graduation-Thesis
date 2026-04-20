package com.skaly.fashion_backend.product.domain.port;

import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.ProductImageEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductImageRepository {
    List<ProductImageEntity> findByProductIdOrderBySortOrderAsc(UUID productId);
    Optional<ProductImageEntity> findByProductIdAndIsPrimaryTrue(UUID productId);
    long countByProductId(UUID productId);
    void clearPrimaryImage(UUID productId);
    ProductImageEntity save(ProductImageEntity image);

    /** Tránh trùng chữ ký {@code findById(UUID)} với {@link ProductRepository}. */
    Optional<ProductImageEntity> findImageById(UUID id);

    void delete(ProductImageEntity image);
}
