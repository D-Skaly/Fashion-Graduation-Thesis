package com.skaly.fashion_backend.payment;

import com.skaly.fashion_backend.order.OrderEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment createPayment(OrderEntity order, PaymentMethod method, BigDecimal amount) {
        Payment payment = Payment.builder()
                .order(order)
                .method(method)
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .currency("VND")
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment created for order {}: {}", order.getId(), saved.getId());
        return saved;
    }

    @Transactional
    public Payment processPayment(UUID paymentId, String transactionId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + paymentId));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment is not in PENDING status");
        }

        payment.markAsPaid(transactionId);
        Payment updated = paymentRepository.save(payment);
        log.info("Payment completed: {}, transaction: {}", paymentId, transactionId);
        return updated;
    }

    @Transactional
    public Payment failPayment(UUID paymentId, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + paymentId));

        payment.markAsFailed(reason);
        Payment updated = paymentRepository.save(payment);
        log.warn("Payment failed: {}, reason: {}", paymentId, reason);
        return updated;
    }

    @Transactional(readOnly = true)
    public Payment getPaymentById(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + id));
    }

    @Transactional(readOnly = true)
    public Payment getPaymentByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order: " + orderId));
    }

    @Transactional(readOnly = true)
    public Payment getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for transaction: " + transactionId));
    }

    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue() {
        return paymentRepository.sumByStatus(PaymentStatus.COMPLETED);
    }

    public static class PaymentNotFoundException extends RuntimeException {
        public PaymentNotFoundException(String message) {
            super(message);
        }
    }
}

