package com.skaly.fashion_backend.user.infrastructure.persistence.jpa;
import com.skaly.fashion_backend.user.BodyProfileEntity;

import com.skaly.fashion_backend.user.BodyProfile;
import com.skaly.fashion_backend.user.BodyProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BodyProfilePersistenceAdapter implements BodyProfileRepository {

    private final JpaBodyProfileRepository jpaBodyProfileRepository;
    private final JpaUserRepository jpaUserRepository;

    @Override
    public Optional<BodyProfile> findByUserId(UUID userId) {
        return jpaBodyProfileRepository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public BodyProfile save(BodyProfile bodyProfile) {
        BodyProfileEntity entity = toEntity(bodyProfile);
        return toDomain(jpaBodyProfileRepository.save(entity));
    }

    private BodyProfile toDomain(BodyProfileEntity entity) {
        return BodyProfile.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .height(entity.getHeight())
                .weight(entity.getWeight())
                .chest(entity.getChest())
                .waist(entity.getWaist())
                .hips(entity.getHips())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private BodyProfileEntity toEntity(BodyProfile domain) {
        return BodyProfileEntity.builder()
                .id(domain.getId())
                .user(jpaUserRepository.findById(domain.getUserId()).orElseThrow())
                .height(domain.getHeight())
                .weight(domain.getWeight())
                .chest(domain.getChest())
                .waist(domain.getWaist())
                .hips(domain.getHips())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
