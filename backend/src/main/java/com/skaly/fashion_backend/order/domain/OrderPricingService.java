package com.skaly.fashion_backend.order.domain;

import com.skaly.fashion_backend.product.interfaces.dto.ProductVariantInternalResponse;

import java.math.BigDecimal;

/**
 * Domain Service - Pure Java, no Spring dependencies
 * Calculation logic for order pricing
 */
public class OrderPricingService {

    public BigDecimal calculateUnitPrice(ProductVariantInternalResponse variant) {
        return variant.price();
    }

    public BigDecimal calculateLineTotal(BigDecimal unitPrice, int quantity) {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
