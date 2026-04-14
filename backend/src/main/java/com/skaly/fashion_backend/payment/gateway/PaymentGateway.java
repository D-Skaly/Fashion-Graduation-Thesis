package com.skaly.fashion_backend.payment.gateway;

import com.skaly.fashion_backend.payment.Payment;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * Interface for payment gateway integrations
 */
public interface PaymentGateway {

    /**
     * Create a payment and return the payment URL or token
     */
    PaymentResponse createPayment(Payment payment, String returnUrl, String ipAddress);

    /**
     * Verify and process the callback from payment gateway
     */
    CallbackResult processCallback(Map<String, String> params);

    /**
     * Query payment status from gateway
     */
    PaymentStatusResponse queryPaymentStatus(String transactionId);

    /**
     * Refund a payment
     */
    RefundResponse refundPayment(String transactionId, BigDecimal amount, String reason);

    /**
     * Get the payment method this gateway handles
     */
    String getPaymentMethod();

    // DTO Classes
    record PaymentResponse(
            boolean success,
            String paymentUrl,
            String transactionId,
            String message
    ) {}

    record CallbackResult(
            boolean success,
            UUID paymentId,
            String transactionId,
            BigDecimal amount,
            String gatewayResponse,
            String message
    ) {}

    record PaymentStatusResponse(
            boolean success,
            String status,
            String message
    ) {}

    record RefundResponse(
            boolean success,
            String refundTransactionId,
            String message
    ) {}
}
