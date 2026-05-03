package com.skaly.fashion_backend.saga.application;

import com.skaly.fashion_backend.order.application.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SagaOrderService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public String getOrderStatus(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(order -> order.getStatus().name())
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Transactional
    public void cancelOrder(UUID orderId, String reason) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.cancel(reason);
            orderRepository.save(order);
        });
    }
}
