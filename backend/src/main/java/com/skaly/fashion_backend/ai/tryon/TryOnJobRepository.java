package com.skaly.fashion_backend.ai.tryon;

import com.skaly.fashion_backend.ai.tryon.TryOnJob;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TryOnJobRepository {
    Optional<TryOnJob> findById(UUID id);
    List<TryOnJob> findByUserId(UUID userId);
    TryOnJob save(TryOnJob job);
    List<TryOnJob> findAllByCreatedAtBeforeAndUserImageUrlIsNotNull(LocalDateTime threshold);
    void delete(TryOnJob job);
}
