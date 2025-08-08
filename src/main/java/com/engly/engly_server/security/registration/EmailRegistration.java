package com.engly.engly_server.security.registration;

import com.engly.engly_server.exception.FieldValidationException;
import com.engly.engly_server.models.dto.create.SignUpRequest;
import com.engly.engly_server.models.entity.AdditionalInfo;
import com.engly.engly_server.models.entity.UserSettings;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Provider;
import com.engly.engly_server.models.enums.Theme;
import com.engly.engly_server.repository.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public final class EmailRegistration implements RegistrationChooser {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${dev.email}")
    private String devEmail;

    @Override
    public Users registration(SignUpRequest signUpRequestDto) {
        try {
            log.info("[AuthService:registerUser]User Registration Started with :::{}", signUpRequestDto);
            userRepository.findByEmail(signUpRequestDto.email()).ifPresent(_ -> {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Already Exist");
            });

            final var users = Users.builder()
                    .roles(signUpRequestDto.email().equals(devEmail) ? "ROLE_ADMIN" : "ROLE_NOT_VERIFIED")
                    .email(signUpRequestDto.email())
                    .imgUrl(signUpRequestDto.imgUrl())
                    .emailVerified(Boolean.FALSE)
                    .username(signUpRequestDto.username())
                    .password(passwordEncoder.encode(signUpRequestDto.password()))
                    .provider(Provider.LOCAL)
                    .lastLogin(Instant.now())
                    .build();

            final var additionalInfo = AdditionalInfo.builder()
                    .user(users)
                    .goal(signUpRequestDto.goals())
                    .englishLevel(signUpRequestDto.englishLevel())
                    .nativeLanguage(signUpRequestDto.nativeLanguage())
                    .build();

            users.setAdditionalInfo(additionalInfo);
            users.setUserSettings(buildUserSettings(users));

            return userRepository.save(users);
        } catch (ValidationException e) {
            log.error("[AuthService:registerUser]User Registration Failed: {}", e.getMessage());
            throw new FieldValidationException(e.getMessage());
        }
    }

    private UserSettings buildUserSettings(Users users) {
        return UserSettings.builder()
                .user(users)
                .theme(Theme.BRIGHT)
                .interfaceLanguage(users.getAdditionalInfo().getNativeLanguage())
                .notifications(true)
                .build();
    }

    @Override
    public Provider getProvider() {
        return Provider.LOCAL;
    }
}
