package com.skaly.fashion_backend.user.domain.entities;

import com.skaly.fashion_backend.user.domain.entities.Provider;
import com.skaly.fashion_backend.user.domain.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String phone;
    private String passwordHash;
    private Role role;
    private Provider provider;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    @Builder.Default
    private Integer loyaltyPoints = 0;

    public void addLoyaltyPoints(int points) {
        if (points > 0) {
            this.loyaltyPoints += points;
        }
    }
}
