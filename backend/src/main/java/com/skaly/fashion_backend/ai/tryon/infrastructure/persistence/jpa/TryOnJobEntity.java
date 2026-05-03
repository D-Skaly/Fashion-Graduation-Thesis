package com.skaly.fashion_backend.ai.tryon.infrastructure.persistence.jpa;

import com.skaly.fashion_backend.ai.tryon.domain.JobStatus;
import com.skaly.fashion_backend.ai.tryon.domain.port.TryOnJob;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity for Try-On job persistence.
 * Maps to "try_on_jobs" table.
 */
@Entity
@Table(name = "try_on_jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TryOnJobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "user_image_url")
    private String userImageUrl;

    @Column(name = "result_image_url")
    private String resultImageUrl;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private String errorMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Converts this entity to the domain model.
     */
    public TryOnJob toDomain() {
        return TryOnJob.builder()
                .id(this.id)
                .userId(this.userId)
                .productId(this.productId)
                .userImageUrl(this.userImageUrl)
                .resultImageUrl(this.resultImageUrl)
                .status(this.status)
                .errorMessage(this.errorMessage)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

    /**
     * Converts a domain model to this entity.
     */
    public static TryOnJobEntity fromDomain(TryOnJob domain) {
        return TryOnJobEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .productId(domain.getProductId())
                .userImageUrl(domain.getUserImageUrl())
                .resultImageUrl(domain.getResultImageUrl())
                .status(domain.getStatus())
                .errorMessage(domain.getErrorMessage())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
