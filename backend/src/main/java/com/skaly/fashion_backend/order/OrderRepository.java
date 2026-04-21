package com.skaly.fashion_backend.order;

import com.skaly.fashion_backend.order.domain.entities.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(UUID id);
    List<Order> findByUserId(UUID userId);
}

