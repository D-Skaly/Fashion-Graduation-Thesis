package com.skaly.fashion_backend.order.infrastructure.persistence.jpa;

import com.skaly.fashion_backend.order.infrastructure.persistence.entities.ShippingEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaShippingRepository extends JpaRepository<ShippingEntity, UUID> {

    Optional<ShippingEntity> findByOrderId(UUID orderId);

    Optional<ShippingEntity> findByTrackingNumber(String trackingNumber);
}
