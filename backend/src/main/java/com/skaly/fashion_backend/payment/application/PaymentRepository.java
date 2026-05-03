package com.skaly.fashion_backend.payment.application;

import com.skaly.fashion_backend.payment.domain.Payment;
import com.skaly.fashion_backend.payment.domain.PaymentStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Payment (Port in Clean Architecture).
 * Lives in payment/application/ (use cases layer).
 */
public interface PaymentRepository {
    
    Payment save(Payment payment);
    
    Optional<Payment> findById(UUID paymentId);
    
    List<Payment> findByOrderId(UUID orderId);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    void deleteById(UUID paymentId);
    
    boolean existsById(UUID paymentId);
}
