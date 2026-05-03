package com.skaly.fashion_backend.cart.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for merging guest cart with user cart.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MergeCartRequest {
    private UUID guestCartId;
    private UUID userCartId;
}
