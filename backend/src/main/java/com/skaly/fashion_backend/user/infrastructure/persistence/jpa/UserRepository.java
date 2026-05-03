package com.skaly.fashion_backend.user.infrastructure.persistence.jpa;

import com.skaly.fashion_backend.user.domain.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    User save(User user);

    long count();
}

