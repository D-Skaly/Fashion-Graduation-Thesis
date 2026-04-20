package com.skaly.fashion_backend.ai.tryon.infrastructure.persistence.jpa;

import com.skaly.fashion_backend.ai.tryon.JobStatus;
import com.skaly.fashion_backend.ai.tryon.TryOnJob;
import com.skaly.fashion_backend.ai.tryon.TryOnJobRepository;
import com.skaly.fashion_backend.user.infrastructure.persistence.jpa.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TryOnJobPersistenceAdapter implements TryOnJobRepository {

    private final JpaTryOnJobRepository jpaTryOnJobRepository;
    private final JpaUserRepository jpaUserRepository;

    @Override
    public Optional<TryOnJob> findById(UUID id) {
        return jpaTryOnJobRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<TryOnJob> findByUserId(UUID userId) {
        return jpaTryOnJobRepository.findByUserId(userId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public TryOnJob save(TryOnJob job) {
        TryOnJobEntity entity = toEntity(job);
        return toDomain(jpaTryOnJobRepository.save(entity));
    }

    @Override
    public List<TryOnJob> findAllByCreatedAtBeforeAndUserImageUrlIsNotNull(LocalDateTime threshold) {
        return jpaTryOnJobRepository.findAllByCreatedAtBeforeAndUserImageUrlIsNotNull(threshold).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(TryOnJob job) {
        jpaTryOnJobRepository.delete(toEntity(job));
    }

    private TryOnJob toDomain(TryOnJobEntity entity) {
        return TryOnJob.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .productId(entity.getProductId())
                .userImageUrl(entity.getUserImageUrl())
                .resultImageUrl(entity.getResultImageUrl())
                .status(JobStatus.valueOf(entity.getStatus().name()))
                .errorMessage(entity.getErrorMessage())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private TryOnJobEntity toEntity(TryOnJob domain) {
        return TryOnJobEntity.builder()
                .id(domain.getId())
                .user(jpaUserRepository.findById(domain.getUserId()).orElseThrow())
                .productId(domain.getProductId())
                .userImageUrl(domain.getUserImageUrl())
                .resultImageUrl(domain.getResultImageUrl())
                .status(TryOnJobEntity.JobStatus.valueOf(domain.getStatus().name()))
                .errorMessage(domain.getErrorMessage())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
