package com.skaly.fashion_backend.user.infrastructure.persistence.jpa;

import com.skaly.fashion_backend.user.infrastructure.persistence.entities.BodyProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface JpaBodyProfileRepository extends JpaRepository<BodyProfileEntity, UUID> {
    Optional<BodyProfileEntity> findByUserId(UUID userId);
}

