package com.skaly.fashion_backend.product.domain.model;
import lombok.*;
import java.util.UUID;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Category {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private UUID parentId;
}

