package com.engly.engly_server.security.jwt.service.impl;

import com.engly.engly_server.models.dto.request.AuthRequest;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.security.config.SecurityContextConfig;
import com.engly.engly_server.security.jwt.JwtHolder;
import com.engly.engly_server.security.jwt.service.JwtAuthenticationService;
import com.engly.engly_server.security.jwt.service.JwtTokenService;
import com.engly.engly_server.security.userconfiguration.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationServiceImpl implements JwtAuthenticationService {
    private final SecurityContextConfig securityContextConfig;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtHolder authentication(Users user, HttpServletResponse response) {
        final var auth = securityContextConfig.createAuthenticationObject(user);
        return generateAndSaveTokens(user, auth, response, true);
    }

    @Override
    public void authenticationForGoogle(Users user, HttpServletResponse response) {
        final var auth = securityContextConfig.createAuthenticationObject(user);
        generateAndSaveTokens(user, auth, response, false);
    }

    @Override
    public JwtHolder authenticationWithParameters(Users user, Authentication authentication, HttpServletResponse response) {
        Authentication properAuth = createProperAuthentication(user, authentication);
        return generateAndSaveTokens(user, properAuth, response, true);
    }

    @Override
    public Authentication authenticateCredentials(AuthRequest.SignInRequest sign) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(sign.email(), sign.password()));
    }

    @Override
    public JwtHolder authenticationForVerification(Users user, HttpServletResponse response) {
        final var auth = securityContextConfig.createAndSetAuthenticationAndReturn(user, user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return generateAndSaveTokens(user, auth, response, true);
    }

    @Override
    public Authentication newAuthentication(Users user) {
        return securityContextConfig.createAndSetAuthenticationAndReturn(user, null);
    }

    private Authentication createProperAuthentication(Users user, Authentication originalAuth) {
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                originalAuth.getCredentials(),
                userDetails.getAuthorities()
        );
    }

    private JwtHolder generateAndSaveTokens(Users user, Authentication auth, HttpServletResponse response, boolean includeAccessToken) {
        log.debug("Generating tokens for user: {} with principal type: {}",
                user.getEmail(), auth.getPrincipal().getClass().getSimpleName());

        final var refreshToken = jwtTokenService.tokenChooser(auth, TokenType.REFRESH);
        final var accessToken = includeAccessToken ? jwtTokenService.tokenChooser(auth, TokenType.ACCESS) : null;

        jwtTokenService.createRefreshTokenCookie(response, refreshToken);
        jwtTokenService.saveRefreshToken(user, refreshToken);

        return includeAccessToken ? new JwtHolder(refreshToken, accessToken) : null;
    }
}
