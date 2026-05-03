package com.skaly.fashion_backend.order.infrastructure;

import com.skaly.fashion_backend.order.OrderUserGateway;
import com.skaly.fashion_backend.user.application.UserInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adapter implementing OrderUserGateway.
 * Cross-module dependency is isolated here in the infrastructure layer,
 * keeping the Order domain/application layers clean.
 */
@Component
@RequiredArgsConstructor
public class OrderUserGatewayAdapter implements OrderUserGateway {

    private final UserInternalService userInternalService;

    @Override
    public UUID getUserIdByEmail(String email) {
        return userInternalService.getUserByEmail(email).id();
    }
}
