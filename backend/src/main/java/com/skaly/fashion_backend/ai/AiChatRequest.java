package com.skaly.fashion_backend.ai;

import jakarta.validation.constraints.NotBlank;

public record AiChatRequest(@NotBlank(message = "message is required") String message) {
}
