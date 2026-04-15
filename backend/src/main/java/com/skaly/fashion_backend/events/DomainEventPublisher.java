package com.skaly.fashion_backend.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class DomainEventPublisher {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Handling OrderCreatedEvent: orderId={}, userId={}, orderNumber={}", 
                event.getOrderId(), event.getUserId(), event.getOrderNumber());
        // Trigger email notification
        // Update analytics
        // Send to message queue for other services
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        log.info("Handling OrderStatusChangedEvent: orderId={}, userId={}, oldStatus={}, newStatus={}", 
                event.getOrderId(), event.getUserId(), event.getOldStatus(), event.getNewStatus());
        // Trigger email notification
        // Update inventory if needed
        // Update analytics
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("Handling PaymentCompletedEvent: paymentId={}, orderId={}, userId={}, amount={}", 
                event.getPaymentId(), event.getOrderId(), event.getUserId(), event.getAmount());
        // Trigger order confirmation email
        // Update order status
        // Update inventory
        // Update analytics
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductStockUpdated(ProductStockUpdatedEvent event) {
        log.info("Handling ProductStockUpdatedEvent: productVariantId={}, productId={}, oldStock={}, newStock={}", 
                event.getProductVariantId(), event.getProductId(), event.getOldStock(), event.getNewStock());
        // Check if stock is low and trigger alert
        // Update cache
        // Notify admin if critical
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Handling UserRegisteredEvent: userId={}, email={}", 
                event.getUserId(), event.getEmail());
        // Send welcome email
        // Create default wishlist
        // Update analytics
    }
}
