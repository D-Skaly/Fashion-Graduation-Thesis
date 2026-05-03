package com.skaly.fashion_backend.payment.application;

import com.skaly.fashion_backend.payment.domain.Payment;
import com.skaly.fashion_backend.payment.domain.PaymentMethod;
import com.skaly.fashion_backend.payment.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service class for payment operations.
 * Lives in payment/application/ (Use Cases layer).
 */
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
    
    /**
     * Initiate a new payment.
     */
    public Payment initiatePayment(UUID orderId, BigDecimal amount, PaymentMethod method) {
        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .orderId(orderId)
                .amount(amount)
                .currency("VND")
                .method(method)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return paymentRepository.save(payment);
    }
    
    /**
     * Update payment status.
     */
    public Payment updatePaymentStatus(UUID paymentId, PaymentStatus status, String transactionId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new com.skaly.fashion_backend.common.domain.ResourceNotFoundException(
                        "Payment not found: " + paymentId));
        
        payment.markAsCompleted(transactionId);
        return paymentRepository.save(payment);
    }
    
    /**
     * Find payment by ID.
     */
    public Payment findById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new com.skaly.fashion_backend.common.domain.ResourceNotFoundException(
                        "Payment not found: " + paymentId));
    }
}
