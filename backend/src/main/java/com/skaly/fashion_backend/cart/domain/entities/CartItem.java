package com.skaly.fashion_backend.cart.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "cartItemBuilder")
public class CartItem {
    private UUID id;
    private UUID productVariantId;
    private Integer quantity;
    private BigDecimal snapshotPrice;
    private LocalDateTime addedAt;
    @Builder.Default
    private boolean quantityAdjusted = false;

    // Custom builder to auto-generate ID and addedAt
    public static CartItemBuilder builder() {
        return cartItemBuilder()
                .id(UUID.randomUUID())
                .addedAt(LocalDateTime.now());
    }

    public static class CartItemBuilder {
        private UUID id = UUID.randomUUID();
        private UUID productVariantId;
        private Integer quantity;
        private BigDecimal snapshotPrice;
        private LocalDateTime addedAt = LocalDateTime.now();
        private boolean quantityAdjusted = false;
        
        public CartItemBuilder id(UUID id) {
            this.id = id;
            return this;
        }
        
        public CartItemBuilder productVariantId(UUID productVariantId) {
            this.productVariantId = productVariantId;
            return this;
        }
        
        public CartItemBuilder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }
        
        public CartItemBuilder snapshotPrice(BigDecimal snapshotPrice) {
            this.snapshotPrice = snapshotPrice;
            return this;
        }
        
        public CartItemBuilder addedAt(LocalDateTime addedAt) {
            this.addedAt = addedAt;
            return this;
        }
        
        public CartItemBuilder quantityAdjusted(boolean quantityAdjusted) {
            this.quantityAdjusted = quantityAdjusted;
            return this;
        }
        
        public CartItem build() {
            return new CartItem(id, productVariantId, quantity, snapshotPrice, addedAt, quantityAdjusted);
        }
    }
}
