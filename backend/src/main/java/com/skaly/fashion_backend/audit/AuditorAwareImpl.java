package com.skaly.fashion_backend.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuditorAwareImpl implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }
        
        // Assuming the principal is the user ID (UUID)
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UUID) {
            return Optional.of((UUID) principal);
        }
        
        // If principal is a String, try to parse as UUID
        if (principal instanceof String) {
            try {
                return Optional.of(UUID.fromString((String) principal));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }
        
        return Optional.empty();
    }
}
