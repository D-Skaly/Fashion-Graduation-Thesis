package com.skaly.fashion_backend.saga.application;

import com.skaly.fashion_backend.payment.domain.Payment;
import com.skaly.fashion_backend.payment.domain.PaymentStatus;
import com.skaly.fashion_backend.saga.application.SagaPaymentService;
import com.skaly.fashion_backend.saga.domain.SagaStep;
import com.skaly.fashion_backend.saga.domain.OrderSagaContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessPaymentStep implements SagaStep<OrderSagaContext> {

    private final SagaPaymentService sagaPaymentService;

    @Override
    public String getStepName() {
        return "ProcessPayment";
    }

    @Override
    public void execute(OrderSagaContext context) throws Exception {
        log.info("Processing payment for order: {}", context.getOrderId());

        Payment payment = sagaPaymentService.completeIfPending(context.getOrderId());

        context.setPayment(payment);
        log.info("Payment processed successfully with status: {}", payment.getStatus());
    }

    @Override
    public void compensate(OrderSagaContext context) {
        log.info("Compensating ProcessPayment: refunding payment for order {}", context.getOrderId());

        try {
            sagaPaymentService.refundIfCompleted(context.getOrderId());
            log.info("Payment refunded successfully");
        } catch (Exception e) {
            log.error("Failed to compensate ProcessPayment", e);
        }
    }

    @Override
    public boolean canCompensate(OrderSagaContext context) {
        Payment payment = sagaPaymentService.findByOrderId(context.getOrderId()).orElse(null);
        return payment != null && payment.getStatus() == PaymentStatus.COMPLETED;
    }
}
