package com.skaly.fashion_backend.order.infrastructure.persistence.jpa;
import com.skaly.fashion_backend.order.OrderNoteEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaOrderNoteRepository extends JpaRepository<OrderNoteEntity, UUID> {

    List<OrderNoteEntity> findByOrderIdOrderByCreatedAtDesc(UUID orderId);

    List<OrderNoteEntity> findByOrderIdAndIsInternalFalseOrderByCreatedAtDesc(UUID orderId);
}
