package com.skaly.fashion_backend.order;

import com.skaly.fashion_backend.events.OrderCreatedEvent;
import com.skaly.fashion_backend.events.OrderStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventService {

    private final ApplicationEventPublisher eventPublisher;

    public void publishOrderCreated(Order order) {
        eventPublisher.publishEvent(new OrderCreatedEvent(
                order.getId(),
                order.getUser().getId(),
                order.getOrderNumber(),
                order.getTotalAmount(),
                order.getStatus().name()));
    }

    public void publishOrderStatusChanged(Order order, OrderStatus oldStatus) {
        eventPublisher.publishEvent(new OrderStatusChangedEvent(
                order.getId(),
                order.getUser().getId(),
                order.getOrderNumber(),
                oldStatus.name(),
                order.getStatus().name()));
    }
}
