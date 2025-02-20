package com.engly.engly_server.utils.mapper.registration_chooser;

import com.engly.engly_server.models.entity.AdditionalInfo;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Provider;
import com.engly.engly_server.models.request.SignUpRequest;
import com.engly.engly_server.repo.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.collections.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Component
@Slf4j
public class EmailRegistration implements RegistrationChooser {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public EmailRegistration(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Pair<Users, AdditionalInfo> registration(SignUpRequest signUpRequest) {
        log.info("[AuthService:registerUser]User Registration Started with :::{}", signUpRequest);
        userRepo.findByEmail(signUpRequest.email()).ifPresent(users -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Already Exist");
        });

        var users = Users.builder()
                .roles("ROLE_USER")
                .createdAt(Instant.now())
                .email(signUpRequest.email())
                .username(signUpRequest.username())
                .password(passwordEncoder.encode(signUpRequest.password()))
                .provider(Provider.LOCAL)
                .build();

        var addInfo = AdditionalInfo.builder()
                .goals(signUpRequest.goals())
                .englishLevel(signUpRequest.englishLevel())
                .gender(signUpRequest.gender())
                .dateOfBirth(signUpRequest.dateOfBirth())
                .nativeLanguage(signUpRequest.nativeLanguage())
                .build();

        users.setAdditionalInfo(addInfo);
        addInfo.setUsers(users);

        Users save = userRepo.save(users);

        return Pair.create(save, save.getAdditionalInfo());
    }

    @Override
    public Provider getProvider() {
        return Provider.LOCAL;
    }
}
