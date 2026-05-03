package com.skaly.fashion_backend.user.interfaces.dto;

public record BodyProfileDto(
    Double height,
    Double weight,
    Double chest,
    Double waist,
    Double hips
) {}
