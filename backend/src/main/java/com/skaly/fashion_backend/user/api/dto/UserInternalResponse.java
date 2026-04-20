package com.skaly.fashion_backend.user.api.dto;

import com.skaly.fashion_backend.user.Role;

import java.util.UUID;

public record UserInternalResponse(
    UUID id,
    String email,
    String firstName,
    String lastName,
    Role role
) {}
