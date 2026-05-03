package com.skaly.fashion_backend.order.infrastructure;

import com.skaly.fashion_backend.order.application.OrderInventoryGateway;
import com.skaly.fashion_backend.order.domain.port.ProductVariantInfo;
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
    public ProductVariantInfo getProductVariant(UUID variantId) {
        ProductVariantInternalResponse response = productInventoryService.getProductVariantInternal(variantId);
        return new ProductVariantInfo(
                response.id(),
                response.productName(),
                response.size(),
                response.color(),
                response.price()
        );
    }

    @Override
    public void reduceStock(UUID variantId, int quantity) {
        productInventoryService.reduceStock(variantId, quantity);
    }
}
