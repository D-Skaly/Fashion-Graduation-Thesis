package com.skaly.fashion_backend.events;

import java.util.UUID;

public class ProductStockUpdatedEvent extends DomainEvent {
    private final UUID productVariantId;
    private final UUID productId;
    private final Integer oldStock;
    private final Integer newStock;
    private final Integer quantityChanged;

    public ProductStockUpdatedEvent(UUID productVariantId, UUID productId, Integer oldStock, Integer newStock, Integer quantityChanged) {
        super("ProductStockUpdated");
        this.productVariantId = productVariantId;
        this.productId = productId;
        this.oldStock = oldStock;
        this.newStock = newStock;
        this.quantityChanged = quantityChanged;
    }

    public UUID getProductVariantId() {
        return productVariantId;
    }

    public UUID getProductId() {
        return productId;
    }

    public Integer getOldStock() {
        return oldStock;
    }

    public Integer getNewStock() {
        return newStock;
    }

    public Integer getQuantityChanged() {
        return quantityChanged;
    }
}
