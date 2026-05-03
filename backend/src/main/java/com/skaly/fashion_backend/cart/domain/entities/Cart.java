package com.skaly.fashion_backend.cart.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "cartBuilder")
public class Cart {
    private UUID id;
    private UUID userId;
    private String guestId;
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();
    private String couponCode;
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CartBuilder builder() {
        return cartBuilder()
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now());
    }

    public static class CartBuilder {
        private UUID id = UUID.randomUUID();
        private UUID userId;
        private String guestId;
        private List<CartItem> items = new ArrayList<>();
        private String couponCode;
        private BigDecimal discountAmount = BigDecimal.ZERO;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();

        public CartBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public CartBuilder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public CartBuilder guestId(String guestId) {
            this.guestId = guestId;
            return this;
        }

        public CartBuilder items(List<CartItem> items) {
            this.items = items != null ? items : new ArrayList<>();
            return this;
        }

        public CartBuilder couponCode(String couponCode) {
            this.couponCode = couponCode;
            return this;
        }

        public CartBuilder discountAmount(BigDecimal discountAmount) {
            this.discountAmount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
            return this;
        }

        public CartBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CartBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Cart build() {
            return new Cart(id, userId, guestId, items, couponCode, discountAmount, createdAt, updatedAt);
        }
    }

    public void addItem(CartItem item) {
        // Check if item with same productVariantId already exists
        for (CartItem existingItem : items) {
            if (existingItem.getProductVariantId().equals(item.getProductVariantId())) {
                // Merge: update quantity
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                existingItem.setQuantityAdjusted(true);
                return;
            }
        }
        // If not found, add new item
        items.add(item);
    }

    public void removeItem(UUID itemId) {
        items.removeIf(item -> item.getId().equals(itemId));
    }

    public void clearItems() {
        items.clear();
    }
}
