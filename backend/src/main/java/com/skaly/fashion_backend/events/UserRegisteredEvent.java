package com.skaly.fashion_backend.events;

import java.util.UUID;

public class UserRegisteredEvent extends DomainEvent {
    private final UUID userId;
    private final String email;
    private final String firstName;
    private final String lastName;

    public UserRegisteredEvent(UUID userId, String email, String firstName, String lastName) {
        super("UserRegistered");
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
