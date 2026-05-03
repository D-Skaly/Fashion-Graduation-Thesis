package com.skaly.fashion_backend.ai.domain.port;

import java.util.Optional;
import java.util.UUID;

/**
 * Port trừu tượng để AI module kiểm tra/khóa user từ User module.
 * <p>
 * Không import JPA entity hay repository implementation — chỉ dùng DTO tối
 * thiểu.
 */
public interface UserLookupPort {

    boolean existsById(UUID userId);

    Optional<UserInfo> findById(UUID userId);

    record UserInfo(UUID id, String email) {
    }
}
