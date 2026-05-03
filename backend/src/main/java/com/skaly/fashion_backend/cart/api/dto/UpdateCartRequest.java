package com.skaly.fashion_backend.cart.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for updating cart item quantity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartRequest {
    private UUID cartItemId;
    private Integer quantity;
}
