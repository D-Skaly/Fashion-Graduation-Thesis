package com.skaly.fashion_backend.cart.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for Cart responses.
 * Used in CartController and CartService.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    private UUID id;
    private UUID userId;
    private BigDecimal totalAmount;
    private Integer totalItems;
    private Boolean isGuestCart;
    private java.util.List<CartItemDto> items;
}
