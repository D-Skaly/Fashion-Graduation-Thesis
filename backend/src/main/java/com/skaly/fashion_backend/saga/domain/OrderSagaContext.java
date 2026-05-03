package com.skaly.fashion_backend.saga.domain;

import com.skaly.fashion_backend.payment.domain.Payment;
import com.skaly.fashion_backend.order.domain.entities.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Context for Order Saga transactions.
 * Carries data between saga steps.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSagaContext {
    private UUID orderId;
    private Order order;
    private Payment payment;
    private String errorMessage;
    private boolean failed;
    
    /**
     * Check if the saga has failed.
     */
    public boolean hasFailed() {
        return failed;
    }
    
    /**
     * Mark the saga as failed with error message.
     */
    public void markAsFailed(String errorMessage) {
        this.failed = true;
        this.errorMessage = errorMessage;
    }
    
    /**
     * Set the created order.
     */
    public void setOrder(Order order) {
        this.order = order;
        this.orderId = order != null ? order.getId() : null;
    }
    
    /**
     * Set the created payment.
     */
    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
