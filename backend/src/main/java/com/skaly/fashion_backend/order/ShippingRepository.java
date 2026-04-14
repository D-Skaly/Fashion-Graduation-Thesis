package com.skaly.fashion_backend.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, UUID> {

    Optional<Shipping> findByOrderId(UUID orderId);

    Optional<Shipping> findByTrackingNumber(String trackingNumber);
}
