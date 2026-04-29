package com.skaly.fashion_backend.ai.tryon.infrastructure.persistence.jpa;

import com.skaly.fashion_backend.ai.tryon.domain.JobStatus;
import com.skaly.fashion_backend.ai.tryon.domain.port.TryOnJob;
import com.skaly.fashion_backend.ai.tryon.domain.port.TryOnJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Persistence adapter for Try-On jobs.
 * Implements the domain port using Spring Data JPA.
 */
@Component
@RequiredArgsConstructor
public class TryOnJobPersistenceAdapter implements TryOnJobRepository {

    private final JpaTryOnJobRepository jpaTryOnJobRepository;

    @Override
    public Optional<TryOnJob> findById(UUID id) {
        return jpaTryOnJobRepository.findById(id).map(TryOnJobEntity::toDomain);
    }

    @Override
    public List<TryOnJob> findByUserId(UUID userId) {
        return jpaTryOnJobRepository.findByUserId(userId).stream()
                .map(TryOnJobEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public TryOnJob save(TryOnJob job) {
        TryOnJobEntity entity = TryOnJobEntity.fromDomain(job);
        return jpaTryOnJobRepository.save(entity).toDomain();
    }

    @Override
    public List<TryOnJob> findAllByCreatedAtBeforeAndUserImageUrlIsNotNull(LocalDateTime threshold) {
        return jpaTryOnJobRepository.findAllByCreatedAtBeforeAndUserImageUrlIsNotNull(threshold).stream()
                .map(TryOnJobEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(TryOnJob job) {
        jpaTryOnJobRepository.delete(TryOnJobEntity.fromDomain(job));
    }
}
