package com.skaly.fashion_backend.order.domain;

import com.skaly.fashion_backend.product.interfaces.dto.ProductVariantInternalResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderPricingServiceTest {

    private final OrderPricingService pricingService = new OrderPricingService();

    @Test
    void calculateUnitPrice_shouldReturnVariantPrice() {
        ProductVariantInternalResponse variant = new ProductVariantInternalResponse(
                "variant-id",
                "product-id",
                "M",
                "Red",
                10,
                new BigDecimal("89.99"),
                "SKU-001"
        );

        BigDecimal result = pricingService.calculateUnitPrice(variant);

        assertEquals(new BigDecimal("89.99"), result);
    }

    @Test
    void calculateLineTotal_shouldMultiplyPriceByQuantity() {
        BigDecimal unitPrice = new BigDecimal("50.00");
        int quantity = 3;

        BigDecimal result = pricingService.calculateLineTotal(unitPrice, quantity);

        assertEquals(new BigDecimal("150.00"), result);
    }

    @Test
    void calculateLineTotal_shouldHandleZeroQuantity() {
        BigDecimal unitPrice = new BigDecimal("50.00");
        int quantity = 0;

        BigDecimal result = pricingService.calculateLineTotal(unitPrice, quantity);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void calculateLineTotal_shouldHandleZeroPrice() {
        BigDecimal unitPrice = BigDecimal.ZERO;
        int quantity = 5;

        BigDecimal result = pricingService.calculateLineTotal(unitPrice, quantity);

        assertEquals(BigDecimal.ZERO, result);
    }
}