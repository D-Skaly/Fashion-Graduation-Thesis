package com.skaly.fashion_backend.saga.application;

import com.skaly.fashion_backend.saga.application.SagaOrderService;
import com.skaly.fashion_backend.saga.domain.SagaStep;
import com.skaly.fashion_backend.saga.domain.OrderSagaContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateOrderStep implements SagaStep<OrderSagaContext> {

    private final SagaOrderService sagaOrderService;

    @Override
    public void execute(OrderSagaContext context) {
        log.info("Creating order with orderNumber: {}", context.getOrderNumber());
        String orderStatus = sagaOrderService.getOrderStatus(context.getOrderId());
        context.setOrderStatus(orderStatus);
        log.info("Order loaded successfully with status: {}", orderStatus);
    }

    @Override
    public void compensate(OrderSagaContext context) {
        log.info("Compensating CreateOrder: cancelling order {}", context.getOrderId());

        try {
            sagaOrderService.cancelOrder(context.getOrderId(), "Saga compensation: CreateOrder rolled back");
            log.info("Order cancelled successfully");
        } catch (Exception e) {
            log.error("Failed to compensate CreateOrder", e);
        }
    }

    @Override
    public boolean canCompensate(OrderSagaContext context) {
        return context.getOrderId() != null;
    }

    @Override
    public String getStepName() {
        return "CreateOrder";
    }
}
