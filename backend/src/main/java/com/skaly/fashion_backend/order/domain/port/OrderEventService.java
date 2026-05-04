package com.skaly.fashion_backend.order.domain.port;

import com.skaly.fashion_backend.order.domain.entities.Order;
import java.util.UUID;

/**
 * Port interface for publishing order-related events.
 * Implemented in order/application/ as OrderEventPublisher.
 */
public interface OrderEventService {

    /**
     * Publish OrderPlacedEvent when order is created.
     */
    void publishOrderPlaced(Order order);

    /**
     * Publish OrderCancelledEvent when order is cancelled.
     */
    void publishOrderCancelled(UUID orderId, String reason);

    /**
     * Publish OrderShippedEvent when order is shipped.
     */
    void publishOrderShipped(UUID orderId, String trackingNumber);

    /**
     * Publish OrderDeliveredEvent when order is delivered.
     */
    void publishOrderDelivered(UUID orderId);

    /**
     * Publish OrderStatusChangedEvent when status changes.
     */
    void publishOrderStatusChanged(UUID orderId, String oldStatus, String newStatus);
}
