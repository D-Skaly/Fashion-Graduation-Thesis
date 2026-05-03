package com.skaly.fashion_backend.cart.interfaces.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartDto(
                UUID id,
                String guestId,
                List<CartItemDto> items,
                String couponCode,
                BigDecimal discountAmount,
                BigDecimal subTotal,
                BigDecimal finalTotal) {
}
