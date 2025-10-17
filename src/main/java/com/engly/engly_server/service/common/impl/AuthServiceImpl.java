package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.request.AuthRequest;
import com.engly.engly_server.models.dto.response.AuthResponseDto;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Provider;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.repository.RefreshTokenRepository;
import com.engly.engly_server.repository.UserRepository;
import com.engly.engly_server.security.jwt.JwtHolder;
import com.engly.engly_server.security.jwt.service.JwtAuthenticationService;
import com.engly.engly_server.security.registration.RegistrationChooser;
import com.engly.engly_server.service.common.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtAuthenticationService jwtAuthenticationService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final Map<Provider, RegistrationChooser> chooserMap;

    @Value("${app.jwt.token.expiry:30}")
    private int tokenExpiryMinutes;

    public AuthServiceImpl(UserRepository userRepository, JwtAuthenticationService jwtAuthenticationService,
                           RefreshTokenRepository refreshTokenRepository, List<RegistrationChooser> choosers) {
        this.userRepository = userRepository;
        this.jwtAuthenticationService = jwtAuthenticationService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.chooserMap = choosers.stream()
                .collect(Collectors.toMap(RegistrationChooser::getProvider, o -> o));
    }

    @Override
    public AuthResponseDto getJwtTokensAfterAuthentication(AuthRequest.SignInRequest signInDto, HttpServletResponse response) {
        var authentication = jwtAuthenticationService.authenticateCredentials(signInDto);
        return userRepository.findByEmail(signInDto.email())
                .map(users -> {
                    users.setLastLogin(Instant.now());

                    var savedUser = userRepository.save(users);
                    var jwtHolder = jwtAuthenticationService.authenticationWithParameters(savedUser, authentication, response);

                    log.info("[AuthService:userSignInAuth] Access token for user:{}, has been generated", users.getUsername());
                    return createAuthResponse(savedUser, jwtHolder);
                })
                .orElseThrow(() -> {
                    log.error("[AuthService:userSignInAuth] User :{} not found", signInDto.email());
                    return new NotFoundException("USER NOT FOUND");
                });
    }


    @Override
    public AuthResponseDto getAccessTokenUsingRefreshToken(String refreshToken, HttpServletResponse response) {
        if (StringUtils.isBlank(refreshToken))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token format");

        var refreshTokenEntity = refreshTokenRepository.findByTokenAndRevokedIsFalse(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Refresh token revoked"));

        var users = refreshTokenEntity.getUser();

        refreshTokenEntity.setRevoked(true);
        refreshTokenRepository.save(refreshTokenEntity);

        var jwtHolder = jwtAuthenticationService.authentication(users, response);

        return createAuthResponse(users, jwtHolder);
    }

    @Override
    public AuthResponseDto registerUser(AuthRequest.SignUpRequest signUpRequestDto, HttpServletResponse httpServletResponse) {
        var user = chooserMap.get(Provider.LOCAL).registration(signUpRequestDto);
        var jwtHolder = jwtAuthenticationService.authentication(user, httpServletResponse);

        log.info("[AuthService:registerUser] User:{} Successfully registered", signUpRequestDto.username());
        return createAuthResponse(user, jwtHolder);
    }

    @Override
    public void processOAuth2PostLogin(String email, String name, String providerId, HttpServletResponse response) {
        var user = userRepository.findByEmail(email)
                .map(existingUser -> {
                    existingUser.setLastLogin(Instant.now());
                    return existingUser;
                })
                .orElseGet(() -> chooserMap.get(Provider.GOOGLE)
                        .registration(AuthRequest.SignUpRequest.builder()
                                .username(name)
                                .email(email)
                                .providerId(providerId)
                                .build()));

        jwtAuthenticationService.authenticationForGoogle(user, response);
    }

    private AuthResponseDto createAuthResponse(Users user, JwtHolder jwtHolder) {
        return AuthResponseDto.builder()
                .accessToken(jwtHolder.accessToken())
                .accessTokenExpiry(tokenExpiryMinutes * 60)
                .username(user.getUsername())
                .tokenType(TokenType.BEARER)
                .build();
    }
}
