package com.skaly.fashion_backend.events;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentCompletedEvent extends DomainEvent {
    private final UUID paymentId;
    private final UUID orderId;
    private final UUID userId;
    private final BigDecimal amount;
    private final String paymentMethod;

    public PaymentCompletedEvent(UUID paymentId, UUID orderId, UUID userId, BigDecimal amount, String paymentMethod) {
        super("PaymentCompleted");
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}
