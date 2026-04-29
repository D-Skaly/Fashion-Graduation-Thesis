package com.skaly.fashion_backend.payment;

import com.skaly.fashion_backend.payment.gateway.PaymentGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceUnitTest {

    @Mock
    private PaymentRepository paymentRepository;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentRepository, List.<PaymentGateway>of());
    }

    @Test
    void processPaymentShouldBeIdempotentWhenAlreadyCompleted() {
        UUID paymentId = UUID.randomUUID();
        Payment payment = Payment.builder()
                .id(paymentId)
                .orderId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .method(PaymentMethod.VNPAY)
                .status(PaymentStatus.COMPLETED)
                .transactionId("txn-1")
                .amount(BigDecimal.valueOf(100_000))
                .build();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        Payment result = paymentService.processPayment(paymentId, "txn-1");

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(result.getTransactionId()).isEqualTo("txn-1");
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void processPaymentShouldCompleteWhenPending() {
        UUID paymentId = UUID.randomUUID();
        Payment payment = Payment.builder()
                .id(paymentId)
                .orderId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .method(PaymentMethod.VNPAY)
                .status(PaymentStatus.PENDING)
                .amount(BigDecimal.valueOf(100_000))
                .build();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.processPayment(paymentId, "gateway-txn");

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(result.getTransactionId()).isEqualTo("gateway-txn");
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void failPaymentShouldIgnoreWhenAlreadyCompleted() {
        UUID paymentId = UUID.randomUUID();
        Payment payment = Payment.builder()
                .id(paymentId)
                .orderId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .method(PaymentMethod.MOMO)
                .status(PaymentStatus.COMPLETED)
                .transactionId("txn-ok")
                .amount(BigDecimal.valueOf(150_000))
                .build();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        Payment result = paymentService.failPayment(paymentId, "Late gateway failure callback");

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void failPaymentShouldBeIdempotentWhenAlreadyFailed() {
        UUID paymentId = UUID.randomUUID();
        Payment payment = Payment.builder()
                .id(paymentId)
                .orderId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .method(PaymentMethod.MOMO)
                .status(PaymentStatus.FAILED)
                .amount(BigDecimal.valueOf(150_000))
                .build();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        Payment result = paymentService.failPayment(paymentId, "Gateway rejected");

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.FAILED);
        verify(paymentRepository, never()).save(any(Payment.class));
    }
}
