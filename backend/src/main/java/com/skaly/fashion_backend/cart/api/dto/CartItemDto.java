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
    private UUID productId;
    private String productName;
    private String variantInfo;
    private Integer quantity;
    private BigDecimal pricePerItem;
    private BigDecimal subtotal;
}
