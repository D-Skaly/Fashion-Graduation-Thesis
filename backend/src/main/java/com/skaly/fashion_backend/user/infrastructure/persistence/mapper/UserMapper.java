package com.skaly.fashion_backend.user.infrastructure.persistence.mapper;

import com.skaly.fashion_backend.user.domain.entities.User;
import com.skaly.fashion_backend.user.infrastructure.persistence.entities.UserEntity;
import org.springframework.stereotype.Component;

/**
 * Chuyển đổi User domain ↔ JPA {@link UserEntity}. Logic mapping chỉ tồn tại ở Infrastructure.
 */
@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .avatarUrl(entity.getAvatarUrl())
                .phone(entity.getPhone())
                .passwordHash(entity.getPasswordHash())
                .role(entity.getRole())
                .provider(entity.getProvider())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .lastLoginAt(entity.getLastLoginAt())
                .loyaltyPoints(entity.getLoyaltyPoints())
                .build();
    }

    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        return UserEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatarUrl(user.getAvatarUrl())
                .phone(user.getPhone())
                .passwordHash(user.getPasswordHash())
                .role(user.getRole())
                .provider(user.getProvider())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .loyaltyPoints(user.getLoyaltyPoints())
                .build();
    }
}
