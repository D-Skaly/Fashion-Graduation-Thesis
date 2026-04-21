package com.skaly.fashion_backend.order;

import com.skaly.fashion_backend.events.OrderCreatedEvent;
import com.skaly.fashion_backend.events.OrderStatusChangedEvent;
import com.skaly.fashion_backend.order.domain.entities.Order;
import com.skaly.fashion_backend.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventService {

    private final OutboxService outboxService;

    public void publishOrderCreated(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getUserId(),
                order.getOrderNumber(),
                order.getTotalAmount(),
                order.getStatus().name());

        outboxService.saveEvent("ORDER", order.getId(), event.getEventType(), event);
    }

    public void publishOrderStatusChanged(Order order, OrderStatus oldStatus) {
        OrderStatusChangedEvent event = new OrderStatusChangedEvent(
                order.getId(),
                order.getUserId(),
                order.getOrderNumber(),
                oldStatus.name(),
                order.getStatus().name());

        outboxService.saveEvent("ORDER", order.getId(), event.getEventType(), event);
    }
}

