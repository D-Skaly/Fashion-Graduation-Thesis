package com.skaly.fashion_backend.user;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {
    private User user;
    private Map<String, Object> attributes;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomOAuth2User(User user, Map<String, Object> attributes,
            Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.attributes = attributes;
        this.authorities = authorities;
    }

    public static CustomOAuth2User create(User user, Map<String, Object> attributes) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        return new CustomOAuth2User(user, attributes, authorities);
    }

    @Override
    public String getName() {
        return user.getEmail();
    }
}

