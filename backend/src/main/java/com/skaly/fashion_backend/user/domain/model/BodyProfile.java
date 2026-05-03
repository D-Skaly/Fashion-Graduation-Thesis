package com.skaly.fashion_backend.user.domain.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model for Body Profile.
 * Used for size recommendation features.
 * Lives in user/domain/model/ (Clean Architecture).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BodyProfile {
    private UUID id;
    private UUID userId;
    private Double height;  // cm
    private Double weight;  // kg
    private Double chest;   // cm
    private Double waist;   // cm
    private Double hips;    // cm
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Calculate BMI (Body Mass Index).
     */
    public Double calculateBMI() {
        if (height == null || weight == null || height == 0) {
            return null;
        }
        double heightM = height / 100.0;
        return weight / (heightM * heightM);
    }
    
    /**
     * Get recommended size based on measurements.
     */
    public String getRecommendedSize() {
        if (chest == null) return "Unknown";
        
        if (chest < 88) return "S";
        else if (chest < 96) return "M";
        else if (chest < 104) return "L";
        else return "XL";
    }
}
