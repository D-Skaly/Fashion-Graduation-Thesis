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
@Builder
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

    public void addItem(CartItem item) {
        items.add(item);
    }

    public void removeItem(UUID itemId) {
        items.removeIf(item -> item.getId().equals(itemId));
    }

    public void clearItems() {
        items.clear();
    }
}
