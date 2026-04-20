package com.skaly.fashion_backend.user;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BodyProfile {
    private UUID id;
    private UUID userId;
    private Double height;
    private Double weight;
    private Double chest;
    private Double waist;
    private Double hips;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

