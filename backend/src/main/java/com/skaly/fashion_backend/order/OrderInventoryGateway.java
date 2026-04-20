package com.skaly.fashion_backend.order;

import com.skaly.fashion_backend.product.ProductVariantInternalResponse;

import java.util.UUID;

public interface OrderInventoryGateway {

    ProductVariantInternalResponse getProductVariant(UUID variantId);

    void reduceStock(UUID variantId, int quantity);
}

