package com.skaly.fashion_backend.user.domain;

import java.time.LocalDateTime;
import java.util.UUID;

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

    public BodyProfile() {
    }

    public BodyProfile(UUID id, UUID userId, Double height, Double weight, Double chest, Double waist, Double hips, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.height = height;
        this.weight = weight;
        this.chest = chest;
        this.waist = waist;
        this.hips = hips;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static BodyProfileBuilder builder() {
        return new BodyProfileBuilder();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getChest() {
        return chest;
    }

    public void setChest(Double chest) {
        this.chest = chest;
    }

    public Double getWaist() {
        return waist;
    }

    public void setWaist(Double waist) {
        this.waist = waist;
    }

    public Double getHips() {
        return hips;
    }

    public void setHips(Double hips) {
        this.hips = hips;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class BodyProfileBuilder {
        private UUID id = UUID.randomUUID();
        private UUID userId;
        private Double height;
        private Double weight;
        private Double chest;
        private Double waist;
        private Double hips;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();

        public BodyProfileBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public BodyProfileBuilder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public BodyProfileBuilder height(Double height) {
            this.height = height;
            return this;
        }

        public BodyProfileBuilder weight(Double weight) {
            this.weight = weight;
            return this;
        }

        public BodyProfileBuilder chest(Double chest) {
            this.chest = chest;
            return this;
        }

        public BodyProfileBuilder waist(Double waist) {
            this.waist = waist;
            return this;
        }

        public BodyProfileBuilder hips(Double hips) {
            this.hips = hips;
            return this;
        }

        public BodyProfileBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BodyProfileBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public BodyProfile build() {
            BodyProfile profile = new BodyProfile();
            profile.setId(id);
            profile.setUserId(userId);
            profile.setHeight(height);
            profile.setWeight(weight);
            profile.setChest(chest);
            profile.setWaist(waist);
            profile.setHips(hips);
            profile.setCreatedAt(createdAt);
            profile.setUpdatedAt(updatedAt);
            return profile;
        }
    }
}

