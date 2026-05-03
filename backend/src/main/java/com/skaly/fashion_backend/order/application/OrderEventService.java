package com.skaly.fashion_backend.order.application;

import com.skaly.fashion_backend.events.OrderCreatedEvent;
import com.skaly.fashion_backend.events.OrderStatusChangedEvent;
import com.skaly.fashion_backend.order.domain.OrderStatus;
import com.skaly.fashion_backend.order.domain.entities.Order;
import com.skaly.fashion_backend.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventService {

    private final OutboxService outboxService;

    public void publishOrderCreated(Order order) {
        var items = order.getItems().stream()
                .map(item -> new OrderCreatedEvent.OrderItemInfo(item.getProductVariantId(), item.getQuantity()))
                .toList();

        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getUserId(),
                order.getOrderNumber(),
                order.getTotalAmount(),
                order.getStatus().name(),
                items);

        outboxService.saveEvent("ORDER", order.getId(), event.getEventType(), event);
    }

    public void publishOrderStatusChanged(Order order, OrderStatus oldStatus) {
        var items = order.getItems().stream()
                .map(item -> new OrderCreatedEvent.OrderItemInfo(item.getProductVariantId(), item.getQuantity()))
                .toList();

        OrderStatusChangedEvent event = new OrderStatusChangedEvent(
                order.getId(),
                order.getUserId(),
                order.getOrderNumber(),
                oldStatus.name(),
                order.getStatus().name(),
                order.getTotalAmount(),
                items);

        outboxService.saveEvent("ORDER", order.getId(), event.getEventType(), event);
    }
}
