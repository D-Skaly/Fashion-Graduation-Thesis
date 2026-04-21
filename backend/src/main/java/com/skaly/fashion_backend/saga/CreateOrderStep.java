package com.skaly.fashion_backend.saga;

import com.skaly.fashion_backend.order.OrderRepository;
import com.skaly.fashion_backend.order.domain.entities.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateOrderStep implements SagaStep<OrderSagaContext> {

    private final OrderRepository orderRepository;

    @Override
    public String getName() {
        return "CreateOrder";
    }

    @Override
    public void execute(OrderSagaContext context) {
        log.info("Creating order with orderNumber: {}", context.getOrderNumber());

        Order order = orderRepository.findById(context.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        context.setOrderStatus(order.getStatus().name());
        log.info("Order loaded successfully with status: {}", order.getStatus());
    }

    @Override
    public void compensate(OrderSagaContext context) {
        log.info("Compensating CreateOrder: cancelling order {}", context.getOrderId());

        try {
            orderRepository.findById(context.getOrderId()).ifPresent(order -> {
                order.cancel("Saga compensation: CreateOrder rolled back");
                orderRepository.save(order);
                log.info("Order cancelled successfully");
            });
        } catch (Exception e) {
            log.error("Failed to compensate CreateOrder", e);
        }
    }

    @Override
    public boolean canCompensate(OrderSagaContext context) {
        return context.getOrderId() != null;
    }
}
