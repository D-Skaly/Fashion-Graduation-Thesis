package com.skaly.fashion_backend.ai.tryon.domain;

import com.skaly.fashion_backend.ai.tryon.domain.port.TryOnJob;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Pure domain port for Try-On job persistence — no JPA annotations here.
 * Infrastructure adapters implement this interface using JPA/whatever.
 */
public interface TryOnJobRepository {

    Optional<TryOnJob> findById(UUID id);

    List<TryOnJob> findByUserId(UUID userId);

    TryOnJob save(TryOnJob job);

    List<TryOnJob> findAllByCreatedAtBeforeAndUserImageUrlIsNotNull(LocalDateTime threshold);

    void delete(TryOnJob job);
}
