package com.skaly.fashion_backend.order.application;

import com.skaly.fashion_backend.product.interfaces.dto.ProductVariantInternalResponse;

import java.util.UUID;

public interface OrderInventoryGateway {

    ProductVariantInternalResponse getProductVariant(UUID variantId);

    void reduceStock(UUID variantId, int quantity);
}
