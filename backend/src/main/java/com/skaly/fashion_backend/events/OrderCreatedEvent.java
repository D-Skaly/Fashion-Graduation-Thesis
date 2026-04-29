package com.skaly.fashion_backend.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.UUID;

public class OrderCreatedEvent extends DomainEvent {
    private final UUID orderId;
    private final UUID userId;
    private final String orderNumber;
    private final BigDecimal totalAmount;
    private final String status;
    private final java.util.List<OrderItemInfo> items;

    @JsonCreator
    public OrderCreatedEvent(
            @JsonProperty("orderId") UUID orderId,
            @JsonProperty("userId") UUID userId,
            @JsonProperty("orderNumber") String orderNumber,
            @JsonProperty("totalAmount") BigDecimal totalAmount,
            @JsonProperty("status") String status,
            @JsonProperty("items") java.util.List<OrderItemInfo> items) {
        super("OrderCreated");
        this.orderId = orderId;
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount;
        this.status = status;
        this.items = items;
    }

    public record OrderItemInfo(UUID variantId, Integer quantity) {}

    public java.util.List<OrderItemInfo> getItems() {
        return items;
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
