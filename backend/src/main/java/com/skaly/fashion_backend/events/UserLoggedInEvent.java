package com.skaly.fashion_backend.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class UserLoggedInEvent extends DomainEvent {
    private final UUID userId;
    private final String email;
    private final String guestId;

    @JsonCreator
    public UserLoggedInEvent(
            @JsonProperty("userId") UUID userId,
            @JsonProperty("email") String email,
            @JsonProperty("guestId") String guestId) {
        super("UserLoggedIn");
        this.userId = userId;
        this.email = email;
        this.guestId = guestId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getGuestId() {
        return guestId;
    }
}
