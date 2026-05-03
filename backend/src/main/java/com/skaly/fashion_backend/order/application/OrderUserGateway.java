package com.skaly.fashion_backend.order.application;

import java.util.UUID;

/**
 * Port for Order module to resolve user identity without cross-module dependency.
 * Implementations live in infrastructure layer.
 */
public interface OrderUserGateway {

    UUID getUserIdByEmail(String email);
}
