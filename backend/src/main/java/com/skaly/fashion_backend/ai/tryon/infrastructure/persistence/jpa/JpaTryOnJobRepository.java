package com.skaly.fashion_backend.ai.tryon.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaTryOnJobRepository extends JpaRepository<TryOnJobEntity, UUID> {
    List<TryOnJobEntity> findByUserId(UUID userId);
    List<TryOnJobEntity> findAllByCreatedAtBeforeAndUserImageUrlIsNotNull(LocalDateTime createdAt);
}
