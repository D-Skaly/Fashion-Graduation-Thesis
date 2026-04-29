package com.skaly.fashion_backend.ai.infrastructure;

import com.skaly.fashion_backend.ai.domain.port.UserLookupPort;
import com.skaly.fashion_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter kết nối AI module với User module qua {@link UserRepository} (port).
 * <p>
 * Đây là điểm <b>duy nhất</b> trong AI module được phép nhìn thấy User module's
 * repository port.
 * Không được inject JPA entity hay JpaRepository trực tiếp.
 */
@Component
@RequiredArgsConstructor
public class UserLookupAdapter implements UserLookupPort {

    private final UserRepository userRepository;

    @Override
    public boolean existsById(UUID userId) {
        return userRepository.findById(userId).isPresent();
    }

    @Override
    public Optional<UserInfo> findById(UUID userId) {
        return userRepository.findById(userId)
                .map(u -> new UserInfo(u.getId(), u.getEmail()));
    }
}
