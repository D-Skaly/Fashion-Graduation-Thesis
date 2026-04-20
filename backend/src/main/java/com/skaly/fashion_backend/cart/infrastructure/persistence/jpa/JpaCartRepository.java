package com.skaly.fashion_backend.cart.infrastructure.persistence.jpa;
import com.skaly.fashion_backend.cart.CartEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaCartRepository extends JpaRepository<CartEntity, UUID> {
    Optional<CartEntity> findByUserId(UUID userId);

    Optional<CartEntity> findByGuestId(String guestId);

    List<CartEntity> findByGuestIdIsNotNullAndUpdatedAtBefore(LocalDateTime date);
}

