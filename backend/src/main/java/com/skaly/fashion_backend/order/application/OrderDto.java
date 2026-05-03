package com.skaly.fashion_backend.order.application;

import com.skaly.fashion_backend.order.domain.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDto(
        UUID id,
        BigDecimal totalAmount,
        OrderStatus status,
        String shippingAddress,
        List<OrderItemDto> items,
        LocalDateTime createdAt) {
}
