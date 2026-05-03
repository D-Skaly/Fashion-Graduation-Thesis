package com.skaly.fashion_backend.auth.interfaces;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank String firstname,
    @NotBlank String lastname,
    @Email @NotBlank String email,
    @NotBlank @Size(min = 6) String password
) {}
