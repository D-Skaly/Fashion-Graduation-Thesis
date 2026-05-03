package com.skaly.fashion_backend.user.domain.model;

import com.skaly.fashion_backend.common.domain.BusinessException;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Custom UserDetails implementation for Spring Security.
 * Used in OAuth2 and JWT authentication.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomOAuth2User implements UserDetails {
    
    private UUID id;
    private String email;
    private String name;
    private String avatarUrl;
    private String password;  // Hashed password
    private boolean enabled = true;
    private java.util.Set<String> roles;  // ROLE_USER, ROLE_ADMIN
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        // Use email as username for authentication
        return email;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Get user ID as string (for JWT).
     */
    public String getIdAsString() {
        return id != null ? id.toString() : null;
    }
    
    /**
     * Check if user has role.
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }
}
