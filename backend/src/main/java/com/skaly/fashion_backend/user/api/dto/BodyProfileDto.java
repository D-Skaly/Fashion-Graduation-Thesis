package com.skaly.fashion_backend.user.api.dto;

public record BodyProfileDto(
    Double height,
    Double weight,
    Double chest,
    Double waist,
    Double hips
) {}
