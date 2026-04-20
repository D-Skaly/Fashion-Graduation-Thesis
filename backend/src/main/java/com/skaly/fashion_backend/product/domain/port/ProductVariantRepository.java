package com.skaly.fashion_backend.product.domain.port;

import com.skaly.fashion_backend.product.domain.model.ProductVariant;

import java.util.Optional;
import java.util.UUID;

public interface ProductVariantRepository {
    Optional<ProductVariant> findVariantById(UUID id);
    ProductVariant save(ProductVariant variant);
}

