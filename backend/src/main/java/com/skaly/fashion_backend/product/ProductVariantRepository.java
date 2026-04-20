package com.skaly.fashion_backend.product;
import java.util.Optional;
import java.util.UUID;
public interface ProductVariantRepository {
    Optional<ProductVariant> findVariantById(UUID id);
    ProductVariant save(ProductVariant variant);
}

