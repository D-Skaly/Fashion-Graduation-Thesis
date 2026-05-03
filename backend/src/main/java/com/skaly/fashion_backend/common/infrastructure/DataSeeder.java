package com.skaly.fashion_backend.common.infrastructure;

import com.skaly.fashion_backend.user.domain.entities.Provider;
import com.skaly.fashion_backend.user.domain.entities.Role;
import com.skaly.fashion_backend.user.domain.entities.User;
import com.skaly.fashion_backend.user.infrastructure.persistence.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        seedUsers();
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .firstName("Admin")
                    .lastName("User")
                    .email("admin@example.com")
                    .passwordHash(passwordEncoder.encode("password"))
                    .role(Role.ADMIN)
                    .provider(Provider.LOCAL)
                    .build();
            userRepository.save(admin);

            User user = User.builder()
                    .firstName("Demo")
                    .lastName("Customer")
                    .email("user@example.com")
                    .passwordHash(passwordEncoder.encode("password"))
                    .role(Role.USER)
                    .provider(Provider.LOCAL)
                    .build();
            userRepository.save(user);
        }
    }
}
