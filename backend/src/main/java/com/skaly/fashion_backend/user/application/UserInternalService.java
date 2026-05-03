package com.skaly.fashion_backend.user.application;

import com.skaly.fashion_backend.common.domain.ResourceNotFoundException;
import com.skaly.fashion_backend.user.interfaces.dto.UserInternalResponse;
import com.skaly.fashion_backend.user.domain.entities.User;
import com.skaly.fashion_backend.user.infrastructure.persistence.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserInternalService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserInternalResponse getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::mapToInternalResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public UserInternalResponse getUserById(UUID id) {
        return userRepository.findById(id)
                .map(this::mapToInternalResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserInternalResponse mapToInternalResponse(User user) {
        return new UserInternalResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        );
    }
}
