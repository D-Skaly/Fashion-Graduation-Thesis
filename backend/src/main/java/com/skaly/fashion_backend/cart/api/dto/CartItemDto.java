package com.skaly.fashion_backend.cart.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for CartItem responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private UUID id;
    private UUID variantId;
    private String productName;
    private String size;
    private String color;
    private BigDecimal currentPrice;
    private BigDecimal snapshotPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private boolean outOfStock;
    private boolean isQuantityAdjusted;
}
