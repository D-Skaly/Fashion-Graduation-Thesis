package com.skaly.fashion_backend.order.domain;

import com.skaly.fashion_backend.order.domain.model.OrderStatusHistory;
import java.util.List;
import java.util.UUID;

/**
 * Domain repository interface for OrderStatusHistory (Port in Clean Architecture).
 * Lives in order/domain/ layer.
 */
public interface OrderStatusHistoryRepository {
    List<OrderStatusHistory> findByOrderIdOrderByChangedAtDesc(UUID orderId);
    OrderStatusHistory save(OrderStatusHistory history);
}
