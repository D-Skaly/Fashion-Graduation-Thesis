package com.skaly.fashion_backend.order;

import com.skaly.fashion_backend.common.ResourceNotFoundException;
import com.skaly.fashion_backend.events.OrderCreatedEvent;
import com.skaly.fashion_backend.order.Order;
import com.skaly.fashion_backend.order.OrderItem;
import com.skaly.fashion_backend.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderInventoryEventHandler {

    private final OrderRepository orderRepository;
    private final OrderInventoryGateway orderInventoryGateway;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleOrderCreated(OrderCreatedEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for stock update: " + event.getOrderId()));

        for (OrderItem item : order.getItems()) {
            orderInventoryGateway.reduceStock(item.getProductVariantId(), item.getQuantity());
        }

        log.info("Stock successfully updated for order {}", event.getOrderId());
    }
}

