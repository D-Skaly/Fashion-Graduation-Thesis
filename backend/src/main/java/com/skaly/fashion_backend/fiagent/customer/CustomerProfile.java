package com.skaly.fashion_backend.fiagent.customer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Customer profile used by FI-Agent (Module 1: AI Stylist).
 *
 * Stores preference vector and tunable personalization weights.
 */
@Entity
@Table(name = "customer_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "customer_id", nullable = false, unique = true)
    private UUID customerId;

    @Column(name = "preferred_style", length = 100)
    private String preferredStyle;

    @Column(name = "budget_min")
    private Double budgetMin;

    @Column(name = "budget_max")
    private Double budgetMax;

    /**
     * Optimization exponent from the specification:
     * S_total = (S_style * M_finance)^w_fit * ...
     */
    @Builder.Default
    @Column(name = "w_fit", nullable = false)
    private Double wFit = 1.0d;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ColumnTransformer(read = "style_preference_vector::text", write = "?::vector")
    @Column(name = "style_preference_vector", columnDefinition = "vector(384)")
    private String stylePreferenceVectorStr;

    public void setStylePreferenceVector(float[] vector) {
        if (vector == null) {
            this.stylePreferenceVectorStr = null;
            return;
        }
        this.stylePreferenceVectorStr = java.util.Arrays.toString(vector);
    }

    public float[] getStylePreferenceVector() {
        if (stylePreferenceVectorStr == null) {
            return null;
        }
        String[] parts = stylePreferenceVectorStr.replace("[", "").replace("]", "").split(",");
        float[] res = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            res[i] = Float.parseFloat(parts[i].trim());
        }
        return res;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
