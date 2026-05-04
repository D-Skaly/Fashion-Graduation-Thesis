package com.skaly.fashion_backend.auth.application;

import com.skaly.fashion_backend.auth.interfaces.dto.AuthenticationRequest;
import com.skaly.fashion_backend.auth.interfaces.dto.AuthenticationResponse;
import com.skaly.fashion_backend.auth.interfaces.dto.RegisterRequest;
import com.skaly.fashion_backend.common.infrastructure.security.JwtUtils;
import com.skaly.fashion_backend.common.infrastructure.security.token.RefreshTokenService;
import com.skaly.fashion_backend.common.infrastructure.security.token.TokenBlacklistService;
import com.skaly.fashion_backend.user.domain.entities.Provider;
import com.skaly.fashion_backend.user.domain.entities.Role;
import com.skaly.fashion_backend.user.domain.entities.User;
import com.skaly.fashion_backend.user.infrastructure.persistence.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    @Value("${application.security.jwt.expiration:86400000}")
    private long jwtExpiration;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request, String deviceInfo, String ipAddress) {
        // ✅ Validate password strength
        validatePasswordStrength(request.getPassword());

        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        userRepository.save(user);
        log.info("User registered: {}", user.getEmail());

        return generateAuthResponse(user, deviceInfo, ipAddress);
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request, String guestId, String deviceInfo, String ipAddress) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));
        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for: {}", request.getEmail());
            throw e;
        }

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        // Update last login time
        user.setLastLoginAt(java.time.LocalDateTime.now());
        userRepository.save(user);

        log.info("User authenticated: {}, GuestID: {}", user.getEmail(), guestId);
        
        // Publish event for other modules (e.g., Cart merging)
        eventPublisher.publishEvent(new com.skaly.fashion_backend.events.UserLoggedInEvent(
                user.getId(), user.getEmail(), guestId));

        return generateAuthResponse(user, deviceInfo, ipAddress);
    }

    @Transactional
    public AuthenticationResponse refreshToken(String refreshToken, String deviceInfo, String ipAddress) {
        var tokenPair = refreshTokenService.rotateRefreshToken(refreshToken, deviceInfo, ipAddress);
        var user = jwtUtils.getUserFromToken(tokenPair.accessToken());

        return new AuthenticationResponse(
                tokenPair.accessToken(),
                tokenPair.refreshToken(),
                jwtExpiration / 1000, // Convert to seconds
                tokenPair.refreshTokenExpiry().toEpochMilli(),
                mapToUserInfo(user)
        );
    }

    @Transactional
    public void logout(String accessToken, String refreshToken) {
        // Blacklist the access token
        var expiryDate = jwtUtils.extractExpiration(accessToken);
        tokenBlacklistService.blacklistToken(accessToken, expiryDate.toInstant());

        // Revoke the refresh token
        if (refreshToken != null) {
            refreshTokenService.revokeToken(refreshToken);
        }

        log.info("User logged out, tokens invalidated");
    }

    @Transactional
    public void logoutAllDevices(String userId) {
        // Revoke all refresh tokens
        refreshTokenService.revokeAllUserTokens(java.util.UUID.fromString(userId));

        // Blacklist all user's access tokens (that we can track)
        var expiryDate = Instant.now().plusMillis(jwtExpiration);
        tokenBlacklistService.blacklistAllUserTokens(userId, expiryDate);

        log.info("All devices logged out for user: {}", userId);
    }

    @Transactional(readOnly = true)
    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("User not found"));
    }

    private AuthenticationResponse generateAuthResponse(User user, String deviceInfo, String ipAddress) {
        var accessToken = jwtUtils.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user, deviceInfo, ipAddress);

        return new AuthenticationResponse(
                accessToken,
                refreshToken.getToken(),
                jwtExpiration / 1000, // Convert to seconds
                refreshToken.getExpiryDate().toEpochMilli(),
                mapToUserInfo(user)
        );
    }

    private AuthenticationResponse.UserInfo mapToUserInfo(User user) {
        return new AuthenticationResponse.UserInfo(
                user.getId().toString(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getAvatarUrl()
        );
    }

    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        
        boolean hasUpper = !password.equals(password.toLowerCase());
        boolean hasLower = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
        
        if (!hasUpper || !hasLower || !hasDigit || !hasSpecial) {
            throw new IllegalArgumentException(
                "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
            );
        }
    }
}
