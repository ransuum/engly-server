package com.engly.engly_server.security.registration;

import com.engly.engly_server.models.dto.request.AuthRequest;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Provider;
import com.engly.engly_server.repository.UserRepository;
import com.engly.engly_server.utils.passwordgenerateutil.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public final class GoogleRegistration implements RegistrationChooser {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Users registration(AuthRequest.@NonNull SignUpRequest signUpRequestDto) {
        log.info("Registering Google user with email: {}", signUpRequestDto.email());
        if (userRepository.existsByEmail(signUpRequestDto.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with this email already exists");
        }

        var user = Users.builder()
                .roles("ROLE_GOOGLE")
                .email(signUpRequestDto.email())
                .emailVerified(Boolean.TRUE)
                .username(signUpRequestDto.username())
                .password(Objects.requireNonNull(passwordEncoder.encode(PasswordUtils.SECURE.generate(16))))
                .provider(Provider.GOOGLE)
                .lastLogin(Instant.now())
                .providerId(signUpRequestDto.providerId())
                .build();
        var usersettings = buildUserSettings(user);
        user.setUserSettings(usersettings);

        return userRepository.save(user);
    }

    @Override
    public Provider getProvider() {
        return Provider.GOOGLE;
    }
}
