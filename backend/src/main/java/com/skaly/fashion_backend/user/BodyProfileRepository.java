package com.skaly.fashion_backend.user;

import com.skaly.fashion_backend.user.BodyProfile;
import java.util.Optional;
import java.util.UUID;

public interface BodyProfileRepository {
    Optional<BodyProfile> findByUserId(UUID userId);
    BodyProfile save(BodyProfile bodyProfile);
}

