package com.skaly.fashion_backend.order.infrastructure;

import com.skaly.fashion_backend.order.application.OrderInventoryGateway;
import com.skaly.fashion_backend.product.application.ProductInventoryService;
import com.skaly.fashion_backend.product.interfaces.dto.ProductVariantInternalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductInventoryGatewayAdapter implements OrderInventoryGateway {

    private final ProductInventoryService productInventoryService;

    @Override
    public ProductVariantInternalResponse getProductVariant(UUID variantId) {
        return productInventoryService.getProductVariantInternal(variantId);
    }

    @Override
    public void reduceStock(UUID variantId, int quantity) {
        productInventoryService.reduceStock(variantId, quantity);
    }
}