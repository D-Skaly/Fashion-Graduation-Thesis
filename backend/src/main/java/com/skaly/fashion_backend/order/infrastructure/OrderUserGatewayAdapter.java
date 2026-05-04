package com.skaly.fashion_backend.order.infrastructure;

import com.skaly.fashion_backend.order.domain.port.OrderUserGateway;
import com.skaly.fashion_backend.user.application.UserInternalService;
import com.skaly.fashion_backend.user.interfaces.dto.UserInternalResponse;
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
    public com.skaly.fashion_backend.user.domain.entities.User getUserById(UUID userId) {
        UserInternalResponse response = userInternalService.getUserById(userId);
        return com.skaly.fashion_backend.user.domain.entities.User.builder()
                .id(response.id())
                .email(response.email())
                .firstName(response.firstName())
                .lastName(response.lastName())
                .role(response.role())
                .build();
    }

    @Override
    public boolean userExists(UUID userId) {
        try {
            userInternalService.getUserById(userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getUserEmail(UUID userId) {
        return userInternalService.getUserById(userId).email();
    }

    @Override
    public String getDefaultShippingAddress(UUID userId) {
        // Placeholder as shipping address is not directly on User entity
        return null;
    }
}
