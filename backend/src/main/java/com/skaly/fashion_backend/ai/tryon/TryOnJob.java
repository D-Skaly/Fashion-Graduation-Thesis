package com.skaly.fashion_backend.ai.tryon;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

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
