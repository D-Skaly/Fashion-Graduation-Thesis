package com.skaly.fashion_backend.payment.application.usecase;

import com.skaly.fashion_backend.payment.domain.Payment;
import com.skaly.fashion_backend.payment.domain.PaymentAccessDeniedException;
import com.skaly.fashion_backend.payment.application.PaymentService;
import com.skaly.fashion_backend.payment.gateway.PaymentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InitiatePaymentUseCase {

    private final PaymentService paymentService;

    public PaymentGateway.PaymentResponse execute(UUID orderId, UUID userId, String returnUrl, String ipAddress) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        if (!payment.getUserId().equals(userId)) {
            throw new PaymentAccessDeniedException("Not authorized to pay for this order");
        }
        return paymentService.initiatePayment(payment.getId(), returnUrl, ipAddress);
    }
}
