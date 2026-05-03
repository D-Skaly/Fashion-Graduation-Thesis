package com.skaly.fashion_backend.product.domain.model;
import lombok.*;
import java.util.UUID;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder(builderMethodName = "categoryBuilder")
public class Category {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private UUID parentId;

    public static CategoryBuilder builder() {
        return categoryBuilder()
                .id(UUID.randomUUID());
    }

    public static class CategoryBuilder {
        private UUID id = UUID.randomUUID();

        public CategoryBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public Category build() {
            return new Category(id, name, slug, description, parentId);
        }
    }
}

