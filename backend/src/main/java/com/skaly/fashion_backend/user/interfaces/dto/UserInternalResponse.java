package com.skaly.fashion_backend.user.interfaces.dto;

import com.skaly.fashion_backend.user.domain.entities.Role;

import java.util.UUID;

public record UserInternalResponse(
    UUID id,
    String email,
    String firstName,
    String lastName,
    Role role
) {}
