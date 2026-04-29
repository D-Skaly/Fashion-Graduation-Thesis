package com.skaly.fashion_backend.payment.infrastructure.gateway;

import com.skaly.fashion_backend.payment.PaymentMethod;
import com.skaly.fashion_backend.payment.domain.port.PaymentGatewayPort;
import com.skaly.fashion_backend.payment.gateway.MomoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class MomoGatewayAdapter implements PaymentGatewayPort {

    private final MomoService momoService;

    @Override
    public PaymentMethod paymentMethod() {
        return PaymentMethod.MOMO;
    }

    @Override
    public CallbackVerificationResult processCallback(Map<String, String> params) {
        var result = momoService.processCallback(params);
        String orderId = params.get("orderId");
        String lookupTransactionId = (orderId != null && !orderId.isBlank()) ? orderId : result.transactionId();
        return new CallbackVerificationResult(
                result.success(),
                lookupTransactionId,
                result.transactionId(),
                result.gatewayResponse(),
                result.message());
    }
}
