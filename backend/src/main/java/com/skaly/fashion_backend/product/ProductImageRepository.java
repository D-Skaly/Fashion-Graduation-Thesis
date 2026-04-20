package com.skaly.fashion_backend.product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface ProductImageRepository {
    List<ProductImageEntity> findByProductIdOrderBySortOrderAsc(UUID productId);
    Optional<ProductImageEntity> findByProductIdAndIsPrimaryTrue(UUID productId);
    long countByProductId(UUID productId);
    void clearPrimaryImage(UUID productId);
    ProductImageEntity save(ProductImageEntity image);
    Optional<ProductImageEntity> findById(UUID id);
    void delete(ProductImageEntity image);
}
