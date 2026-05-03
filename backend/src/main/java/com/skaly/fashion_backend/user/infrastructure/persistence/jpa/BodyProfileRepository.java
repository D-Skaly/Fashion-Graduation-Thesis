package com.skaly.fashion_backend.user.infrastructure.persistence.jpa;

import com.skaly.fashion_backend.user.domain.model.BodyProfile;
import java.util.Optional;
import java.util.UUID;

public interface BodyProfileRepository {
    Optional<BodyProfile> findByUserId(UUID userId);
    BodyProfile save(BodyProfile bodyProfile);
}

