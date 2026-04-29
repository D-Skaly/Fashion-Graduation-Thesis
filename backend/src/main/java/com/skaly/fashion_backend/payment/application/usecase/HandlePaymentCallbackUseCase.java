package com.skaly.fashion_backend.payment.application.usecase;

import com.skaly.fashion_backend.payment.Payment;
import com.skaly.fashion_backend.payment.PaymentMethod;
import com.skaly.fashion_backend.payment.PaymentService;
import com.skaly.fashion_backend.payment.domain.port.PaymentGatewayPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HandlePaymentCallbackUseCase {

    private final PaymentService paymentService;
    private final PaymentCallbackLedgerService paymentCallbackLedgerService;
    private final Map<PaymentMethod, PaymentGatewayPort> gatewayPorts;

    public HandlePaymentCallbackUseCase(PaymentService paymentService,
            PaymentCallbackLedgerService paymentCallbackLedgerService,
            List<PaymentGatewayPort> gatewayPorts) {
        this.paymentService = paymentService;
        this.paymentCallbackLedgerService = paymentCallbackLedgerService;
        this.gatewayPorts = new EnumMap<>(PaymentMethod.class);
        gatewayPorts.forEach(port -> this.gatewayPorts.put(port.paymentMethod(), port));
    }

    public CallbackHandleResult execute(PaymentMethod method, Map<String, String> params) {
        PaymentGatewayPort gatewayPort = gatewayPorts.get(method);
        if (gatewayPort == null) {
            return new CallbackHandleResult(false, "Gateway not configured for method: " + method, 500);
        }

        var verificationResult = gatewayPort.processCallback(params);
        boolean firstSeen = paymentCallbackLedgerService.registerIfFirstSeen(
                method,
                verificationResult.lookupTransactionId(),
                verificationResult.settledTransactionId(),
                verificationResult.success(),
                params.toString());

        if (!firstSeen) {
            return new CallbackHandleResult(true, "Duplicate callback ignored", 200);
        }

        if (!verificationResult.success()) {
            markFailureIfPaymentExists(verificationResult.lookupTransactionId(), verificationResult.message());
            return new CallbackHandleResult(false, verificationResult.message(), 200);
        }

        try {
            Payment payment = paymentService.getPaymentByTransactionId(verificationResult.lookupTransactionId());
            String settledTransactionId = verificationResult.settledTransactionId() != null
                    ? verificationResult.settledTransactionId()
                    : verificationResult.lookupTransactionId();
            paymentService.processPayment(payment.getId(), settledTransactionId);
            return new CallbackHandleResult(true, "Payment successful", 200);
        } catch (Exception e) {
            log.error("Error updating payment state after successful callback", e);
            return new CallbackHandleResult(true, "Payment processed but error updating state", 200);
        }
    }

    private void markFailureIfPaymentExists(String lookupTransactionId, String reason) {
        if (lookupTransactionId == null || lookupTransactionId.isBlank()) {
            return;
        }
        try {
            Payment payment = paymentService.getPaymentByTransactionId(lookupTransactionId);
            paymentService.failPayment(payment.getId(), reason);
        } catch (Exception ex) {
            log.warn("Could not mark payment as failed for transaction {}: {}", lookupTransactionId, ex.getMessage());
        }
    }

    public record CallbackHandleResult(boolean success, String message, int httpStatus) {
    }
}
