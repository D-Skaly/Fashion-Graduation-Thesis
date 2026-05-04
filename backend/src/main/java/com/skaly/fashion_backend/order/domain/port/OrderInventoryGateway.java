package com.skaly.fashion_backend.order.domain.port;

import com.skaly.fashion_backend.order.domain.entities.OrderItem;
import java.util.UUID;

/**
 * Port interface for checking and updating inventory.
 * Implemented in product module as OrderInventoryGatewayAdapter.
 */
public interface OrderInventoryGateway {

    /**
     * Check if sufficient stock exists for all items in an order.
     */
    boolean checkStock(UUID productVariantId, int quantity);

    /**
     * Reserve stock for items (before order confirmation).
     */
    void reserveStock(UUID productVariantId, int quantity);

    /**
     * Confirm stock deduction after order is placed.
     */
    void confirmStockDeduction(UUID productVariantId, int quantity);

    /**
     * Release reserved stock (if order fails).
     */
    void releaseStock(UUID productVariantId, int quantity);

    /**
     * Get product variant information.
     */
    ProductVariantInfo getProductVariant(UUID productVariantId);

    /**
     * Get current stock quantity for a product variant.
     */
    Integer getStock(UUID productVariantId);
}
