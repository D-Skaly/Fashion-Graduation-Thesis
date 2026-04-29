package com.skaly.fashion_backend.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final List<com.skaly.fashion_backend.payment.gateway.PaymentGateway> gateways;

    @Transactional
    public Payment createPayment(UUID orderId, UUID userId, PaymentMethod method, BigDecimal amount) {
        Payment payment = Payment.builder()
                .orderId(orderId)
                .userId(userId)
                .method(method)
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .currency("VND")
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment created for order {} for user {}: {}", orderId, userId, saved.getId());
        return saved;
    }

    @Transactional
    public com.skaly.fashion_backend.payment.gateway.PaymentGateway.PaymentResponse initiatePayment(UUID paymentId,
            String returnUrl, String ipAddress) {
        Payment payment = getPaymentById(paymentId);

        com.skaly.fashion_backend.payment.gateway.PaymentGateway gateway = gateways.stream()
                .filter(g -> g.getPaymentMethod().equalsIgnoreCase(payment.getMethod().name()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No gateway found for method: " + payment.getMethod()));

        var response = gateway.createPayment(payment, returnUrl, ipAddress);

        if (response.success()) {
            payment.setTransactionId(response.transactionId());
            paymentRepository.save(payment);
        }

        return response;
    }

    @Transactional
    public Payment processPayment(UUID paymentId, String transactionId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + paymentId));

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            if (!Objects.equals(payment.getTransactionId(), transactionId)) {
                log.warn(
                        "Duplicate payment callback with different transactionId. paymentId={}, existing={}, incoming={}",
                        paymentId, payment.getTransactionId(), transactionId);
            }
            return payment;
        }

        if (payment.getStatus() != PaymentStatus.PENDING && payment.getStatus() != PaymentStatus.PROCESSING) {
            throw new IllegalStateException("Payment cannot be completed from status: " + payment.getStatus());
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

        if (payment.getStatus() == PaymentStatus.FAILED) {
            return payment;
        }

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            log.warn("Ignoring failure callback for completed payment: {}", paymentId);
            return payment;
        }

        if (payment.getStatus() != PaymentStatus.PENDING && payment.getStatus() != PaymentStatus.PROCESSING) {
            log.warn("Ignoring failure callback for payment {} in status {}", paymentId, payment.getStatus());
            return payment;
        }

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

    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByUserId(UUID userId) {
        return paymentRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Payment> getAllPayments(PaymentStatus status) {
        if (status != null) {
            return paymentRepository.findByStatus(status);
        }
        return paymentRepository.findAll();
    }
}
