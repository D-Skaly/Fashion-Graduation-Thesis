package com.skaly.fashion_backend.payment.domain.port;

import com.skaly.fashion_backend.payment.PaymentMethod;

import java.util.Map;

public interface PaymentGatewayPort {

    PaymentMethod paymentMethod();

    CallbackVerificationResult processCallback(Map<String, String> params);

    record CallbackVerificationResult(
            boolean success,
            String lookupTransactionId,
            String settledTransactionId,
            String gatewayResponse,
            String message) {
    }
}
