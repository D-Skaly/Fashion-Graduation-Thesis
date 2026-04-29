package com.skaly.fashion_backend.saga.application;

import com.skaly.fashion_backend.payment.Payment;
import com.skaly.fashion_backend.payment.PaymentRepository;
import com.skaly.fashion_backend.payment.PaymentService;
import com.skaly.fashion_backend.payment.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SagaPaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    @Transactional(readOnly = true)
    public Optional<Payment> findByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    @Transactional
    public Payment completeIfPending(UUID orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.PENDING || payment.getStatus() == PaymentStatus.PROCESSING) {
            String transactionId = payment.getTransactionId() != null
                    ? payment.getTransactionId()
                    : "SAGA-" + orderId;
            return paymentService.processPayment(payment.getId(), transactionId);
        }

        return payment;
    }

    @Transactional
    public void refundIfCompleted(UUID orderId) {
        paymentRepository.findByOrderId(orderId).ifPresent(payment -> {
            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                payment.markAsRefunded();
                paymentRepository.save(payment);
            }
        });
    }
}
