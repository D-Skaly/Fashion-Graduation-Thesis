package com.skaly.fashion_backend.saga.application;

import com.skaly.fashion_backend.product.application.ProductInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SagaInventoryService {

    private final ProductInventoryService productInventoryService;

    @Transactional(readOnly = true)
    public int getCurrentStock(UUID variantId) {
        return productInventoryService.getCurrentStock(variantId);
    }

    @Transactional
    public void reduceStock(UUID variantId, int quantity) {
        productInventoryService.reduceStock(variantId, quantity);
    }

    @Transactional
    public void restoreStock(UUID variantId, int originalStock) {
        int currentStock = productInventoryService.getCurrentStock(variantId);
        int diff = originalStock - currentStock;
        if (diff > 0) {
            productInventoryService.addStock(variantId, diff);
        }
    }
}
