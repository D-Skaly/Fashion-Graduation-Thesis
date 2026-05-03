package com.skaly.fashion_backend.product.application.event;

import com.skaly.fashion_backend.events.OrderCreatedEvent;
import com.skaly.fashion_backend.events.OrderStatusChangedEvent;
import com.skaly.fashion_backend.product.application.ProductInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductOrderEventListener {

    private final ProductInventoryService inventoryService;

    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Handling OrderCreatedEvent for inventory reduction. OrderId: {}", event.getOrderId());
        for (OrderCreatedEvent.OrderItemInfo item : event.getItems()) {
            try {
                inventoryService.reduceStock(item.variantId(), item.quantity());
            } catch (Exception e) {
                log.error("Failed to reduce stock for variantId: {} in order: {}. Reason: {}", 
                        item.variantId(), event.getOrderId(), e.getMessage());
                // In a production Saga, you would trigger a compensating action here (cancel order)
            }
        }
    }

    @EventListener
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        String newStatus = event.getNewStatus();
        if ("CANCELLED".equals(newStatus) || "REFUNDED".equals(newStatus)) {
            log.info("Handling {} for inventory compensation. OrderId: {}", newStatus, event.getOrderId());
            for (OrderCreatedEvent.OrderItemInfo item : event.getItems()) {
                try {
                    inventoryService.addStock(item.variantId(), item.quantity());
                } catch (Exception e) {
                    log.error("Failed to compensate stock for variantId: {} in order: {}. Reason: {}", 
                            item.variantId(), event.getOrderId(), e.getMessage());
                }
            }
        }
    }
}
