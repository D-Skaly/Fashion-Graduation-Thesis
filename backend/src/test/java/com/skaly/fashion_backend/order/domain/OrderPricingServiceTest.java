package com.skaly.fashion_backend.order.domain;

import com.skaly.fashion_backend.product.interfaces.dto.ProductVariantInternalResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderPricingServiceTest {

    @Test
    void calculateUnitPrice_shouldReturnPrice() {
        ProductVariantInternalResponse variant = new ProductVariantInternalResponse(
                UUID.randomUUID(), // id
                UUID.randomUUID(), // productId
                "Test Product", // productName
                "M", // size
                "Red", // color
                new BigDecimal("89.99"), // price
                10 // stockQuantity
        );

        OrderPricingService pricingService = new OrderPricingService();
        BigDecimal result = pricingService.calculateUnitPrice(variant);
        assertEquals(new BigDecimal("89.99"), result);
    }

    @Test
    void calculateLineTotal_shouldMultiplyPriceByQuantity() {
        OrderPricingService pricingService = new OrderPricingService();
        BigDecimal unitPrice = new BigDecimal("50.00");
        int quantity = 3;

        BigDecimal result = pricingService.calculateLineTotal(unitPrice, quantity);
        assertEquals(new BigDecimal("150.00"), result);
    }

    @Test
    void calculateLineTotal_shouldHandleZeroQuantity() {
        OrderPricingService pricingService = new OrderPricingService();
        BigDecimal unitPrice = new BigDecimal("50.00");
        int quantity = 0;

        BigDecimal result = pricingService.calculateLineTotal(unitPrice, quantity);
        assertEquals(0, result.compareTo(BigDecimal.ZERO));
    }

    @Test
    void calculateLineTotal_shouldHandleZeroPrice() {
        OrderPricingService pricingService = new OrderPricingService();
        BigDecimal unitPrice = BigDecimal.ZERO;
        int quantity = 5;

        BigDecimal result = pricingService.calculateLineTotal(unitPrice, quantity);
        assertEquals(0, result.compareTo(BigDecimal.ZERO));
    }
}