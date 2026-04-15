package com.skaly.fashion_backend.events;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderCreatedEvent extends DomainEvent {
    private final UUID orderId;
    private final UUID userId;
    private final String orderNumber;
    private final BigDecimal totalAmount;
    private final String status;

    public OrderCreatedEvent(UUID orderId, UUID userId, String orderNumber, BigDecimal totalAmount, String status) {
        super("OrderCreated");
        this.orderId = orderId;
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }
}
