package com.skaly.fashion_backend.product.interfaces.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductVariantDto(
                @NotNull(message = "Variant ID is required")
                java.util.UUID id,

                @NotBlank(message = "Size is required")
                String size,

                @NotBlank(message = "Color is required")
                String color,

                @NotNull(message = "Stock quantity is required")
                Integer stockQuantity,

                @DecimalMin(value = "0.0", message = "Price adjustment must be non-negative")
                BigDecimal priceAdjustment,

                @NotBlank(message = "SKU code is required")
                String skuCode) {
}

