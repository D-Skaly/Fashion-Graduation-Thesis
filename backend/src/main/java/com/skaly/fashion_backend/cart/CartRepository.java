package com.skaly.fashion_backend.cart;

import com.skaly.fashion_backend.cart.Cart;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository {
    Optional<Cart> findByUserId(UUID userId);
    Optional<Cart> findByGuestId(String guestId);
    Optional<Cart> findById(UUID id);
    Cart save(Cart cart);
    void delete(Cart cart);
    List<Cart> findAbandonedGuestCarts(LocalDateTime thresholdDate);
}

