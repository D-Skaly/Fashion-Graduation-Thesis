package com.skaly.fashion_backend.payment.domain;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model for Payment.
 * Lives in payment/domain/ (Clean Architecture).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private UUID id;
    private UUID orderId;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod method;
    private PaymentStatus status;
    private String transactionId;  // External transaction ID
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Check if payment is successful.
     */
    public boolean isSuccessful() {
        return status == PaymentStatus.COMPLETED;
    }
    
    /**
     * Check if payment can be retried.
     */
    public boolean canRetry() {
        return status == PaymentStatus.FAILED || status == PaymentStatus.CANCELED;
    }
    
    /**
     * Mark payment as completed.
     */
    public void markAsCompleted(String transactionId) {
        this.status = PaymentStatus.COMPLETED;
        this.transactionId = transactionId;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Mark payment as failed.
     */
    public void markAsFailed(String errorMessage) {
        this.status = PaymentStatus.FAILED;
        this.errorMessage = errorMessage;
        this.updatedAt = LocalDateTime.now();
    }
}
