package com.skaly.fashion_backend.user;

import com.skaly.fashion_backend.user.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    User save(User user);
}

