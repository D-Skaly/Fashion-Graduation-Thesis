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
@Builder
public class CartItem {
    private UUID id;
    private UUID productVariantId;
    private Integer quantity;
    private BigDecimal snapshotPrice;
    private LocalDateTime addedAt;
    @Builder.Default
    private boolean quantityAdjusted = false;
}
