package com.skaly.fashion_backend.auth.interfaces.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
    @Email @NotBlank String email,
    @NotBlank String password
) {}
