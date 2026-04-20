package com.skaly.fashion_backend.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class OrderStatusChangedEvent extends DomainEvent {
    private final UUID orderId;
    private final UUID userId;
    private final String orderNumber;
    private final String oldStatus;
    private final String newStatus;

    @JsonCreator
    public OrderStatusChangedEvent(
            @JsonProperty("orderId") UUID orderId,
            @JsonProperty("userId") UUID userId,
            @JsonProperty("orderNumber") String orderNumber,
            @JsonProperty("oldStatus") String oldStatus,
            @JsonProperty("newStatus") String newStatus) {
        super("OrderStatusChanged");
        this.orderId = orderId;
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
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

    public String getOldStatus() {
        return oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }
}
