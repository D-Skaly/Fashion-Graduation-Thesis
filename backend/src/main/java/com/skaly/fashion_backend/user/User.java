package com.skaly.fashion_backend.user;

import lombok.*;
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
}

