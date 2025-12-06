package com.engly.engly_server.security.admindata;

import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Provider;
import com.engly.engly_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.password}")
    private String password;

    @Override
    public void run(@NotNull String... args) {
        if (!userRepository.existsByEmail("admin@admin.com")) {
            final var adminUser = Users.builder()
                    .username("admin")
                    .email("admin@admin.com")
                    .password(Objects.requireNonNull(passwordEncoder.encode(password)))
                    .roles("ROLE_SYSADMIN")
                    .emailVerified(true)
                    .provider(Provider.LOCAL)
                    .build();

            userRepository.save(adminUser);
        }
    }
}
