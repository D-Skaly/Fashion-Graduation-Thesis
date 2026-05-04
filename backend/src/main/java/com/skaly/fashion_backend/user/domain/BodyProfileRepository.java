package com.skaly.fashion_backend.user.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for BodyProfile (Port in Clean Architecture).
 * Lives in user/domain/ layer.
 */
public interface BodyProfileRepository {
    
    Optional<BodyProfile> findByUserId(UUID userId);
    
    BodyProfile save(BodyProfile bodyProfile);
}
