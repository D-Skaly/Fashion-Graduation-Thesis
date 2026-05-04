package com.skaly.fashion_backend.order.domain.port;

import com.skaly.fashion_backend.user.domain.entities.User;
import java.util.UUID;

/**
 * Port interface for Order module to access User data.
 * Implemented in user module as OrderUserGatewayAdapter.
 */
public interface OrderUserGateway {

    /**
     * Get user by ID.
     */
    User getUserById(UUID userId);

    /**
     * Check if user exists.
     */
    boolean userExists(UUID userId);

    /**
     * Get user email by ID.
     */
    String getUserEmail(UUID userId);

    /**
     * Get user's default shipping address.
     */
    String getDefaultShippingAddress(UUID userId);
}
