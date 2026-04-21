package com.skaly.fashion_backend.cart.infrastructure.persistence.jpa;
import com.skaly.fashion_backend.cart.infrastructure.persistence.entities.CartItemEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaCartItemRepository extends JpaRepository<CartItemEntity, UUID> {
}
