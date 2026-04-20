package com.skaly.fashion_backend.user.infrastructure.persistence.jpa;
import com.skaly.fashion_backend.user.BodyProfileEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaBodyProfileRepository extends JpaRepository<BodyProfileEntity, UUID> {
    Optional<BodyProfileEntity> findByUserId(UUID userId);
}
