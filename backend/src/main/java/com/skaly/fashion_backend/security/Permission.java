package com.skaly.fashion_backend.security;

import java.util.UUID;

public class Permission {
    private UUID id;
    private String name;
    private String description;
    private String resource;  // e.g., "product", "order", "user"
    private String action;    // e.g., "read", "write", "delete", "manage"

    public Permission() {
    }

    public Permission(String name, String description, String resource, String action) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.resource = resource;
        this.action = action;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
