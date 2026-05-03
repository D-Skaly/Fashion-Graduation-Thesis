package com.skaly.fashion_backend.order.application;

import com.skaly.fashion_backend.order.domain.port.ProductVariantInfo;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public interface OrderInventoryGateway {

    ProductVariantInfo getProductVariant(UUID variantId);

    void reduceStock(UUID variantId, int quantity);

    default Map<UUID, ProductVariantInfo> getProductVariantsBatch(List<UUID> variantIds) {
        return variantIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        this::getProductVariant
                ));
    }
}
