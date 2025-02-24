package com.engly.engly_server.utils.registration_chooser;

import com.engly.engly_server.models.entity.AdditionalInfo;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Provider;
import com.engly.engly_server.models.request.SignUpRequest;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.utils.password_generate_util.PasswordGeneratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.collections.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Slf4j
public class GoogleRegistration implements RegistrationChooser {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public GoogleRegistration(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Pair<Users, AdditionalInfo> registration(SignUpRequest signUpRequest) {
        log.info("Registering Google user with email: {}", signUpRequest.email());
        userRepo.findByEmail(signUpRequest.email()).ifPresent(users -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with this Google email already exists");
        });

        Users user = Users.builder()
                .roles("ROLE_GOOGLE")
                .email(signUpRequest.email())
                .emailVerified(Boolean.TRUE)
                .username(signUpRequest.username())
                .password(passwordEncoder.encode(
                        PasswordGeneratorUtil.generatePassword(signUpRequest.email(), signUpRequest.username()))
                )
                .provider(Provider.GOOGLE)
                .providerId(signUpRequest.providerId())
                .build();

        return Pair.create(userRepo.save(user), null);
    }

    @Override
    public Provider getProvider() {
        return Provider.GOOGLE;
    }
}
