package com.skaly.fashion_backend.user.interfaces.dto;

import java.util.UUID;

/**
 * DTO for BodyProfile data transfer between layers.
 */
public class BodyProfileDto {

    private UUID id;
    private UUID userId;
    private Double height; // cm
    private Double weight; // kg
    private Double chest; // cm
    private Double waist; // cm
    private Double hip; // cm
    private Double inseam; // cm
    private String bodyType; // Slim, Athletic, Curvy, etc.
    private String recommendedSize; // S, M, L, XL, etc.

    public BodyProfileDto() {
    }

    public BodyProfileDto(UUID id, UUID userId, Double height, Double weight, 
                         Double chest, Double waist, Double hip, Double inseam, 
                         String bodyType, String recommendedSize) {
        this.id = id;
        this.userId = userId;
        this.height = height;
        this.weight = weight;
        this.chest = chest;
        this.waist = waist;
        this.hip = hip;
        this.inseam = inseam;
        this.bodyType = bodyType;
        this.recommendedSize = recommendedSize;
    }

    // Convenience constructor for body measurements only
    public BodyProfileDto(Double height, Double weight, Double chest, Double waist, Double hips) {
        this.height = height;
        this.weight = weight;
        this.chest = chest;
        this.waist = waist;
        this.hip = hips;
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

    public Double getHip() {
        return hip;
    }

    public void setHip(Double hip) {
        this.hip = hip;
    }

    public Double getInseam() {
        return inseam;
    }

    public void setInseam(Double inseam) {
        this.inseam = inseam;
    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public String getRecommendedSize() {
        return recommendedSize;
    }

    public void setRecommendedSize(String recommendedSize) {
        this.recommendedSize = recommendedSize;
    }
}
