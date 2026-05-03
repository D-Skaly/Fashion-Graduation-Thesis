package com.skaly.fashion_backend.order.application;

import com.skaly.fashion_backend.order.infrastructure.persistence.entities.OrderNoteEntity;

import java.util.List;
import java.util.UUID;

public interface OrderNoteRepository {
    List<OrderNoteEntity> findOrderNotesByOrderIdOrderByCreatedAtDesc(UUID orderId);
    OrderNoteEntity save(OrderNoteEntity note);
}
