package com.skaly.fashion_backend.order.application;

import com.skaly.fashion_backend.order.infrastructure.persistence.entities.OrderStatusHistoryEntity;

import java.util.List;
import java.util.UUID;

public interface OrderStatusHistoryRepository {
    List<OrderStatusHistoryEntity> findByOrderIdOrderByCreatedAtDesc(UUID orderId);
    OrderStatusHistoryEntity save(OrderStatusHistoryEntity history);
}
