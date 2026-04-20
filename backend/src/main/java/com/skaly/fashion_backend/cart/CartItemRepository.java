package com.skaly.fashion_backend.cart;

import com.skaly.fashion_backend.cart.CartItem;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository {
    Optional<CartItem> findItemById(UUID id);
}

