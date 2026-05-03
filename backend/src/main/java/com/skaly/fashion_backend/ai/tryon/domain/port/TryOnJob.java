package com.skaly.fashion_backend.ai.tryon.domain.port;

import com.skaly.fashion_backend.ai.tryon.domain.JobStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Pure domain model for Try-On job — no JPA annotations.
 * Persistence details live in infrastructure layer.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TryOnJob {
    private UUID id;
    private UUID userId;
    private UUID productId;
    private String userImageUrl;
    private String resultImageUrl;
    private JobStatus status;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
