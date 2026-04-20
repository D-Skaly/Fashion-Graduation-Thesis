package com.skaly.fashion_backend.order;
import java.util.List;
import java.util.UUID;
public interface OrderNoteRepository {
    List<OrderNoteEntity> findByOrderIdOrderByCreatedAtDesc(UUID orderId);
    OrderNoteEntity save(OrderNoteEntity note);
}
