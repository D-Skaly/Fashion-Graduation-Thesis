package com.skaly.fashion_backend.saga;

import com.skaly.fashion_backend.payment.Payment;
import com.skaly.fashion_backend.payment.PaymentRepository;
import com.skaly.fashion_backend.payment.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessPaymentStep implements SagaStep<OrderSagaContext> {

    private final PaymentRepository paymentRepository;

    @Override
    public String getName() {
        return "ProcessPayment";
    }

    @Override
    public void execute(OrderSagaContext context) {
        log.info("Processing payment for order: {}", context.getOrderId());
        
        Payment payment = paymentRepository.findByOrderId(context.getOrderId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        // Simulate payment processing
        if (payment.getStatus() == PaymentStatus.PENDING) {
            payment.setStatus(PaymentStatus.COMPLETED);
            paymentRepository.save(payment);
        }
        
        context.setPayment(payment);
        log.info("Payment processed successfully with status: {}", payment.getStatus());
    }

    @Override
    public void compensate(OrderSagaContext context) {
        log.info("Compensating ProcessPayment: refunding payment for order {}", context.getOrderId());
        
        try {
            Payment payment = paymentRepository.findByOrderId(context.getOrderId())
                    .orElse(null);
            
            if (payment != null && payment.getStatus() == PaymentStatus.COMPLETED) {
                payment.setStatus(PaymentStatus.REFUNDED);
                paymentRepository.save(payment);
                log.info("Payment refunded successfully");
            }
        } catch (Exception e) {
            log.error("Failed to compensate ProcessPayment", e);
        }
    }

    @Override
    public boolean canCompensate(OrderSagaContext context) {
        Payment payment = paymentRepository.findByOrderId(context.getOrderId()).orElse(null);
        return payment != null && payment.getStatus() == PaymentStatus.COMPLETED;
    }
}
