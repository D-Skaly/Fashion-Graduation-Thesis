package com.skaly.fashion_backend.payment.application;

import com.skaly.fashion_backend.common.domain.ResourceNotFoundException;
import com.skaly.fashion_backend.payment.domain.Payment;
import com.skaly.fashion_backend.payment.domain.PaymentMethod;
import com.skaly.fashion_backend.payment.domain.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    
    @Transactional
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
        
        log.info("Payment initiated: {} for order: {}", payment.getId(), orderId);
        return paymentRepository.save(payment);
    }
    
    @Transactional
    public Payment updatePaymentStatus(UUID paymentId, PaymentStatus status, String transactionId) {
        Payment payment = findById(paymentId);
        
        if (status == PaymentStatus.COMPLETED) {
            payment.markAsCompleted(transactionId);
        } else {
            payment.setStatus(status);
            payment.setUpdatedAt(LocalDateTime.now());
        }
        
        Payment saved = paymentRepository.save(payment);
        log.info("Payment {} status updated to: {}", paymentId, status);
        return saved;
    }
    
    public Payment findById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found: " + paymentId));
    }
    
    @Transactional
    public void deletePayment(UUID paymentId) {
        Payment payment = findById(paymentId);
        paymentRepository.delete(payment);
        log.info("Payment deleted: {}", paymentId);
    }
}
