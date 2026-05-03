package com.skaly.fashion_backend.order.application;

import com.skaly.fashion_backend.order.infrastructure.persistence.entities.ShippingEntity;

import java.util.Optional;
import java.util.UUID;

public interface ShippingRepository {
    Optional<ShippingEntity> findByOrderId(UUID orderId);
    ShippingEntity save(ShippingEntity shipping);
}
