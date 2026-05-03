package com.skaly.fashion_backend.payment.infrastructure.gateway;

import com.skaly.fashion_backend.payment.domain.PaymentMethod;
import com.skaly.fashion_backend.payment.domain.port.PaymentGatewayPort;
import com.skaly.fashion_backend.payment.gateway.VNPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class VnPayGatewayAdapter implements PaymentGatewayPort {

    private final VNPayService vnPayService;

    @Override
    public PaymentMethod paymentMethod() {
        return PaymentMethod.VNPAY;
    }

    @Override
    public CallbackVerificationResult processCallback(Map<String, String> params) {
        var result = vnPayService.processCallback(params);
        String txnRef = params.get("vnp_TxnRef");
        String lookupTransactionId = (txnRef != null && !txnRef.isBlank()) ? txnRef : result.transactionId();
        return new CallbackVerificationResult(
                result.success(),
                lookupTransactionId,
                result.transactionId(),
                result.gatewayResponse(),
                result.message());
    }
}
