package com.skaly.fashion_backend.order.domain.model;

import com.skaly.fashion_backend.order.domain.OrderStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain entity for OrderStatusHistory.
 * Lives in order/domain/model/ layer.
 */
@Data
@Builder(toBuilder = true)
public class OrderStatusHistory {
    private UUID id;
    private UUID orderId;
    private OrderStatus oldStatus;
    private OrderStatus newStatus;
    private String changedBy;
    private String note;
    private LocalDateTime changedAt;
}
