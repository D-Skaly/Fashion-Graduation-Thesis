package com.skaly.fashion_backend.order;

import com.skaly.fashion_backend.product.ProductInventoryService;
import com.skaly.fashion_backend.product.ProductVariant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductInventoryGatewayAdapter implements OrderInventoryGateway {

    private final ProductInventoryService productInventoryService;

    @Override
    public ProductVariant getProductVariant(UUID variantId) {
        return productInventoryService.getProductVariantById(variantId);
    }

    @Override
    public void reduceStock(UUID variantId, int quantity) {
        productInventoryService.reduceStock(variantId, quantity);
    }
}
