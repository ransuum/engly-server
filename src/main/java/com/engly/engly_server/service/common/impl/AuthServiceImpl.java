package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.dto.create.SignInDto;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.*;
import com.engly.engly_server.models.dto.create.SignUpRequestDto;
import com.engly.engly_server.repo.RefreshTokenRepo;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.security.jwt.JwtHolder;
import com.engly.engly_server.security.jwt.service.JwtAuthenticationService;
import com.engly.engly_server.service.common.AuthService;
import com.engly.engly_server.security.registration.RegistrationChooser;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepo;
    private final JwtAuthenticationService jwtAuthenticationService;
    private final RefreshTokenRepo refreshTokenRepo;
    private final Map<Provider, RegistrationChooser> chooserMap;

    @Value("${app.jwt.token.expiry:30}")
    private int tokenExpiryMinutes;

    private final BiFunction<Users, JwtHolder, AuthResponseDto> createAuthResponse = (user, jwtHolder) ->
            AuthResponseDto.builder()
                    .accessToken(jwtHolder.accessToken())
                    .accessTokenExpiry(tokenExpiryMinutes * 60)
                    .username(user.getUsername())
                    .tokenType(TokenType.Bearer)
                    .build();

    public AuthServiceImpl(UserRepo userRepo, JwtAuthenticationService jwtAuthenticationService,
                           RefreshTokenRepo refreshTokenRepo, List<RegistrationChooser> choosers) {
        this.userRepo = userRepo;
        this.jwtAuthenticationService = jwtAuthenticationService;
        this.refreshTokenRepo = refreshTokenRepo;
        this.chooserMap = choosers.stream()
                .collect(Collectors.toMap(RegistrationChooser::getProvider, o -> o));
    }

    @Override
    public AuthResponseDto getJwtTokensAfterAuthentication(SignInDto signInDto, HttpServletResponse response) {
        final var authentication = jwtAuthenticationService.authenticateCredentials(signInDto);
        return userRepo.findByEmail(signInDto.email())
                .map(users -> {
                    users.setLastLogin(Instant.now());

                    final var savedUser = userRepo.save(users);
                    final var jwtHolder = jwtAuthenticationService.authenticateData(savedUser, authentication, response);

                    log.info("[AuthService:userSignInAuth] Access token for user:{}, has been generated", users.getUsername());
                    return createAuthResponse.apply(savedUser, jwtHolder);
                })
                .orElseThrow(() -> {
                    log.error("[AuthService:userSignInAuth] User :{} not found", signInDto.email());
                    return new NotFoundException("USER NOT FOUND");
                });
    }


    @Override
    public AuthResponseDto getAccessTokenUsingRefreshToken(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token format");

        final var refreshTokenEntity = refreshTokenRepo.findByTokenAndRevokedIsFalse(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Refresh token revoked"));
        refreshTokenEntity.setRevoked(true);
        final var users = refreshTokenRepo.save(refreshTokenEntity).getUser();

        final var jwtHolder = jwtAuthenticationService.createAuthObject(users, response);

        return createAuthResponse.apply(users, jwtHolder);
    }

    @Override
    public AuthResponseDto registerUser(SignUpRequestDto signUpRequestDto, HttpServletResponse httpServletResponse) {
        final var user = chooserMap.get(Provider.LOCAL).registration(signUpRequestDto);
        final var jwtHolder = jwtAuthenticationService.createAuthObject(user, httpServletResponse);

        log.info("[AuthService:registerUser] User:{} Successfully registered", signUpRequestDto.username());
        return createAuthResponse.apply(user, jwtHolder);
    }
}
