package com.engly.engly_server.security.registration;

import com.engly.engly_server.exception.FieldValidationException;
import com.engly.engly_server.models.dto.request.AuthRequest;
import com.engly.engly_server.models.entity.AdditionalInfo;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Provider;
import com.engly.engly_server.repository.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
@NullMarked
public final class EmailRegistration implements RegistrationChooser {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Users registration(AuthRequest.SignUpRequest signUpRequestDto) {
        try {
            log.info("[AuthService:registerUser]User Registration Started with :::{}", signUpRequestDto);
            if (userRepository.existsByEmail(signUpRequestDto.email())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with this email already exists");
            }

            var users = Users.builder()
                    .roles("ROLE_NOT_VERIFIED")
                    .email(signUpRequestDto.email())
                    .imgUrl(signUpRequestDto.imgUrl())
                    .emailVerified(Boolean.FALSE)
                    .username(signUpRequestDto.username())
                    .password(Objects.requireNonNull(passwordEncoder.encode(signUpRequestDto.password())))
                    .provider(Provider.LOCAL)
                    .lastLogin(Instant.now())
                    .build();
            var additionalInfo = buildAdditionalInfo(users, signUpRequestDto);
            users.setAdditionalInfo(additionalInfo);
            users.setUserSettings(buildUserSettings(users));

            return userRepository.save(users);
        } catch (ValidationException e) {
            log.error("[AuthService:registerUser]User Registration Failed: {}", e.getMessage());
            throw new FieldValidationException(e.getMessage());
        }
    }

    private AdditionalInfo buildAdditionalInfo(Users users, AuthRequest.SignUpRequest signUpRequestDto) {
        return AdditionalInfo.builder()
                .user(users)
                .goal(signUpRequestDto.goals())
                .englishLevel(signUpRequestDto.englishLevel())
                .nativeLanguage(signUpRequestDto.nativeLanguage())
                .build();
    }

    @Override
    public Provider getProvider() {
        return Provider.LOCAL;
    }
}
