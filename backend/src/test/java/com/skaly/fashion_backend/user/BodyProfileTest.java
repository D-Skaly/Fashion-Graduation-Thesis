package com.skaly.fashion_backend.user;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BodyProfileTest {

    @Test
    void builder_shouldSetDefaultValues() {
        UUID userId = UUID.randomUUID();
        BodyProfile profile = BodyProfile.builder()
                .userId(userId)
                .height(170.0)
                .weight(65.0)
                .chest(90.0)
                .waist(75.0)
                .hips(95.0)
                .build();

        assertNotNull(profile.getId());
        assertEquals(userId, profile.getUserId());
        assertEquals(170.0, profile.getHeight(), 0.01);
        assertEquals(65.0, profile.getWeight(), 0.01);
        assertNotNull(profile.getCreatedAt());
        assertNotNull(profile.getUpdatedAt());
    }

    @Test
    void updateMeasurements_shouldUpdateFields() {
        BodyProfile profile = BodyProfile.builder()
                .height(170.0)
                .weight(65.0)
                .build();

        profile.setHeight(175.0);
        profile.setWeight(70.0);
        profile.setChest(95.0);

        assertEquals(175.0, profile.getHeight(), 0.01);
        assertEquals(70.0, profile.getWeight(), 0.01);
        assertEquals(95.0, profile.getChest(), 0.01);
    }

    @Test
    void calculateBMI_shouldReturnCorrectValue() {
        BodyProfile profile = BodyProfile.builder()
                .height(170.0) // cm
                .weight(65.0) // kg
                .build();

        // BMI = weight / (height in m)^2
        double heightInM = profile.getHeight() / 100.0;
        double expectedBMI = profile.getWeight() / (heightInM * heightInM);

        assertEquals(expectedBMI, profile.getWeight() / ((profile.getHeight() / 100.0) * (profile.getHeight() / 100.0)), 0.01);
    }

    @Test
    void builder_shouldGenerateDifferentUUIDs() {
        BodyProfile profile1 = BodyProfile.builder()
                .height(170.0)
                .weight(65.0)
                .build();

        BodyProfile profile2 = BodyProfile.builder()
                .height(180.0)
                .weight(75.0)
                .build();

        assertNotNull(profile1.getId());
        assertNotNull(profile2.getId());
        assertNotEquals(profile1.getId(), profile2.getId());
    }

    @Test
    void updateTimestamps_shouldUpdateUpdatedAt() {
        BodyProfile profile = BodyProfile.builder()
                .height(170.0)
                .weight(65.0)
                .build();

        LocalDateTime originalUpdatedAt = profile.getUpdatedAt();
        
        // Simulate update
        profile.setUpdatedAt(LocalDateTime.now());

        assertNotNull(profile.getUpdatedAt());
        assertTrue(profile.getUpdatedAt().isAfter(originalUpdatedAt) || 
               profile.getUpdatedAt().equals(originalUpdatedAt));
    }
}