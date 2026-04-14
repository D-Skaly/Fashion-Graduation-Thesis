package com.skaly.fashion_backend.auth;

import com.skaly.fashion_backend.security.JwtUtils;
import com.skaly.fashion_backend.security.token.RefreshTokenService;
import com.skaly.fashion_backend.security.token.TokenBlacklistService;
import com.skaly.fashion_backend.user.Provider;
import com.skaly.fashion_backend.user.Role;
import com.skaly.fashion_backend.user.User;
import com.skaly.fashion_backend.user.UserRepository;
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

    @Value("${application.security.jwt.expiration:86400000}")
    private long jwtExpiration;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request, String deviceInfo, String ipAddress) {
        // Check if user already exists
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        var user = User.builder()
                .firstName(request.firstname())
                .lastName(request.lastname())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        userRepository.save(user);
        log.info("User registered: {}", user.getEmail());

        return generateAuthResponse(user, deviceInfo, ipAddress);
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request, String deviceInfo, String ipAddress) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()));
        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for: {}", request.email());
            throw e;
        }

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        // Update last login time
        user.setLastLoginAt(java.time.LocalDateTime.now());
        userRepository.save(user);

        log.info("User authenticated: {}", user.getEmail());

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
                tokenPair.refreshTokenExpiry(),
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

    private AuthenticationResponse generateAuthResponse(User user, String deviceInfo, String ipAddress) {
        var accessToken = jwtUtils.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user, deviceInfo, ipAddress);

        return new AuthenticationResponse(
                accessToken,
                refreshToken.getToken(),
                jwtExpiration / 1000, // Convert to seconds
                refreshToken.getExpiryDate(),
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
}
