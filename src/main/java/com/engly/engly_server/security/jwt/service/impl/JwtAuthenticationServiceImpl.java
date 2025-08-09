package com.engly.engly_server.security.jwt.service.impl;

import com.engly.engly_server.models.dto.request.SignInRequest;
import com.engly.engly_server.models.entity.RefreshToken;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.repository.RefreshTokenRepository;
import com.engly.engly_server.security.jwt.JwtHolder;
import com.engly.engly_server.security.jwt.JwtTokenGenerator;
import com.engly.engly_server.security.jwt.service.JwtAuthenticationService;
import com.engly.engly_server.security.userconfiguration.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationServiceImpl implements JwtAuthenticationService {
    private static final int REFRESH_TOKEN_VALIDITY_DAYS = 25;

    private final JwtTokenGenerator jwtTokenGenerator;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtHolder createAuthObject(Users user, HttpServletResponse response) {
        final var auth = createAuthentication(user);
        return generateAndSaveTokens(user, auth, response, true);
    }

    @Override
    public void createAuthObjectForGoogle(Users user, HttpServletResponse response) {
        final var auth = createAuthentication(user);
        generateAndSaveTokens(user, auth, response, false);
    }

    @Override
    public JwtHolder authenticateData(Users user, Authentication authentication, HttpServletResponse response) {
        return generateAndSaveTokens(user, authentication, response, true);
    }

    @Override
    public Authentication authenticateCredentials(SignInRequest sign) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(sign.email(), sign.password()));
    }

    @Override
    public JwtHolder createAuthObjectForVerification(Users user, HttpServletResponse response) {
        final var auth = createAndSetAuthentication(user, user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return generateAndSaveTokens(user, auth, response, true);
    }

    @Override
    public Authentication newAuthentication(Users user) {
        return createAndSetAuthentication(user, null);
    }

    private JwtHolder generateAndSaveTokens(Users user, Authentication auth, HttpServletResponse response, boolean includeAccessToken) {
        final var refreshToken = jwtTokenGenerator.generateRefreshToken(auth);
        final var accessToken = includeAccessToken ? jwtTokenGenerator.generateAccessToken(auth) : null;

        jwtTokenGenerator.createRefreshTokenCookie(response, refreshToken);
        saveRefreshToken(user, refreshToken);

        return includeAccessToken ? new JwtHolder(refreshToken, accessToken) : null;
    }

    private void saveRefreshToken(Users user, String refreshToken) {
        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plus(REFRESH_TOKEN_VALIDITY_DAYS, ChronoUnit.DAYS))
                .revoked(false)
                .build());
    }

    private Authentication createAuthentication(Users user) {
        return jwtTokenGenerator.createAuthenticationObject(user);
    }

    private Authentication createAndSetAuthentication(Users user, String password) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(), password, new UserDetailsImpl(user).getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return auth;
    }
}
