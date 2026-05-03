package com.skaly.fashion_backend.common.infrastructure.security.token;

import com.skaly.fashion_backend.security.JwtUtils;
import com.skaly.fashion_backend.user.domain.entities.User;
import com.skaly.fashion_backend.user.infrastructure.persistence.entities.UserEntity;
import com.skaly.fashion_backend.user.infrastructure.persistence.jpa.JpaUserRepository;
import com.skaly.fashion_backend.user.infrastructure.persistence.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;
    private final JpaUserRepository jpaUserRepository;
    private final UserMapper userMapper;

    @Value("${application.security.refresh-token.expiration:604800000}") // 7 days default
    private long refreshTokenExpirationMs;

    @Value("${application.security.refresh-token.max-tokens-per-user:5}")
    private int maxTokensPerUser;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public RefreshToken createRefreshToken(User user, String deviceInfo, String ipAddress) {
        // Clean up old tokens if exceeding limit
        enforceMaxTokensLimit(user.getId());

        UserEntity userEntity = jpaUserRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + user.getId()));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(generateToken());
        refreshToken.setUser(userEntity);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMs));
        refreshToken.setDeviceInfo(deviceInfo);
        refreshToken.setIpAddress(ipAddress);
        refreshToken.setIsRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public TokenPair rotateRefreshToken(String oldToken, String deviceInfo, String ipAddress) {
        RefreshToken oldRefreshToken = refreshTokenRepository.findByToken(oldToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        if (!oldRefreshToken.isValid()) {
            throw new InvalidRefreshTokenException("Refresh token is expired or revoked");
        }

        User user = userMapper.toDomain(oldRefreshToken.getUser());

        // Revoke old token
        revokeToken(oldToken);

        // Create new refresh token
        RefreshToken newRefreshToken = createRefreshToken(user, deviceInfo, ipAddress);

        // Generate new access token
        String accessToken = jwtUtils.generateToken(user);

        log.info("Token rotated successfully for user: {}", user.getEmail());

        return new TokenPair(accessToken, newRefreshToken.getToken(), newRefreshToken.getExpiryDate());
    }

    @Transactional
    public void revokeToken(String token) {
        int updated = refreshTokenRepository.revokeByToken(token);
        if (updated > 0) {
            log.info("Refresh token revoked: {}", token.substring(0, 10) + "...");
        }
    }

    @Transactional
    public void revokeAllUserTokens(UUID userId) {
        int updated = refreshTokenRepository.revokeAllByUserId(userId);
        log.info("Revoked {} refresh tokens for user {}", updated, userId);
    }

    @Transactional
    public void cleanupExpiredTokens() {
        int deleted = refreshTokenRepository.deleteAllExpiredOrRevoked(Instant.now());
        log.info("Cleaned up {} expired/revoked refresh tokens", deleted);
    }

    private void enforceMaxTokensLimit(UUID userId) {
        long validTokenCount = refreshTokenRepository.countByUserIdAndIsRevokedFalseAndExpiryDateAfter(
                userId, Instant.now());

        if (validTokenCount >= maxTokensPerUser) {
            // Get oldest tokens and revoke them
            List<RefreshToken> tokens = refreshTokenRepository.findAllValidByUserId(userId, Instant.now());
            if (tokens.size() >= maxTokensPerUser) {
                // Revoke oldest tokens (by creation date)
                tokens.stream()
                        .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                        .limit(tokens.size() - maxTokensPerUser + 1)
                        .forEach(t -> {
                            t.setIsRevoked(true);
                            refreshTokenRepository.save(t);
                        });
            }
        }
    }

    private String generateToken() {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpirationMs;
    }

    public record TokenPair(String accessToken, String refreshToken, Instant refreshTokenExpiry) {}

    public static class InvalidRefreshTokenException extends RuntimeException {
        public InvalidRefreshTokenException(String message) {
            super(message);
        }
    }
}