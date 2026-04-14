package com.skaly.fashion_backend.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderNoteRepository extends JpaRepository<OrderNote, UUID> {

    List<OrderNote> findByOrderIdOrderByCreatedAtDesc(UUID orderId);

    List<OrderNote> findByOrderIdAndIsInternalFalseOrderByCreatedAtDesc(UUID orderId);
}
