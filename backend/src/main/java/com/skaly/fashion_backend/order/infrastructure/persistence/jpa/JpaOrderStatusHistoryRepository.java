package com.skaly.fashion_backend.order.infrastructure.persistence.jpa;

import com.skaly.fashion_backend.order.OrderStatusHistoryEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaOrderStatusHistoryRepository extends JpaRepository<OrderStatusHistoryEntity, UUID> {

    List<OrderStatusHistoryEntity> findByOrderIdOrderByCreatedAtDesc(UUID orderId);
}
