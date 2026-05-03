package com.skaly.fashion_backend.user.infrastructure.persistence.jpa;

import com.skaly.fashion_backend.user.infrastructure.persistence.jpa.UserRepository;
import com.skaly.fashion_backend.user.domain.entities.User;
import com.skaly.fashion_backend.user.infrastructure.persistence.entities.UserEntity;
import com.skaly.fashion_backend.user.infrastructure.persistence.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.StructuredTaskScope;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepository {

    private final JpaUserRepository jpaUserRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<User> findById(UUID id) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(() -> jpaUserRepository.findById(id).map(userMapper::toDomain));
            scope.join();
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find user by id", e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(() -> jpaUserRepository.findByEmail(email).map(userMapper::toDomain));
            scope.join();
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find user by email", e);
        }
    }

    @Override
    public User save(User user) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(() -> {
                UserEntity entity = userMapper.toEntity(user);
                return userMapper.toDomain(jpaUserRepository.save(entity));
            });
            scope.join();
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }

    @Override
    public long count() {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(() -> jpaUserRepository.count());
            scope.join();
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to count users", e);
        }
    }
}
