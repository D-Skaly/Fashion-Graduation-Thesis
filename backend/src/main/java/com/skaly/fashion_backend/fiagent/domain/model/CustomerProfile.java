package com.skaly.fashion_backend.fiagent.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerProfile {

    private UUID id;

    private UUID customerId;

    private String preferredStyle;

    private Double budgetMin;

    private Double budgetMax;

    /**
     * Optimization exponent from the specification:
     * S_total = (S_style * M_finance)^w_fit * ...
     */
    @Builder.Default
    private Double wFit = 1.0d;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

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
}
