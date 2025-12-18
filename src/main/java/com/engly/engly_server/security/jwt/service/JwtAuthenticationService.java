package com.engly.engly_server.security.jwt.service;

import com.engly.engly_server.models.dto.request.AuthRequest;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.security.config.SecurityContextConfig;
import com.engly.engly_server.security.jwt.JwtHolder;
import com.engly.engly_server.security.userconfiguration.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@NullMarked
public class JwtAuthenticationService {

    private final SecurityContextConfig securityContextConfig;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

    public JwtHolder authentication(Users user, HttpServletResponse response) {
        var auth = securityContextConfig.createAuthenticationObject(user);
        return generateAndSaveTokens(user, auth, response, true);
    }

    public void authenticationForGoogle(Users user, HttpServletResponse response) {
        var auth = securityContextConfig.createAuthenticationObject(user);
        generateAndSaveTokens(user, auth, response, false);
    }

    public JwtHolder authenticationWithParameters(Users user, Authentication authentication, HttpServletResponse response) {
        var properAuth = createProperAuthentication(user, authentication);
        return generateAndSaveTokens(user, properAuth, response, true);
    }

    public Authentication authenticateCredentials(AuthRequest.SignInRequest sign) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(sign.email(), sign.password()));
    }

    public JwtHolder authenticationForVerification(Users user, HttpServletResponse response) {
        var auth = securityContextConfig.createAndSetAuthenticationAndReturn(user, user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return generateAndSaveTokens(user, auth, response, true);
    }

    public Authentication newAuthentication(Users user) {
        return securityContextConfig.createAndSetAuthenticationAndReturn(user, null);
    }

    private Authentication createProperAuthentication(Users user, Authentication originalAuth) {
        UserDetails userDetails = new UserDetailsImpl(user);
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                originalAuth.getCredentials(),
                userDetails.getAuthorities()
        );
    }

    private JwtHolder generateAndSaveTokens(Users user, Authentication auth,
                                                      HttpServletResponse response, boolean includeAccessToken) {
        log.debug("Generating tokens for user: {} with principal type: {}",
                user.getEmail(), Objects.requireNonNull(auth.getPrincipal()).getClass().getSimpleName());

        var refreshToken = jwtTokenService.tokenChooser(auth, TokenType.REFRESH);
        var accessToken = includeAccessToken ? jwtTokenService.tokenChooser(auth, TokenType.ACCESS) : StringUtils.EMPTY;

        jwtTokenService.createRefreshTokenCookie(response, refreshToken);
        jwtTokenService.saveRefreshToken(user, refreshToken);

        return new JwtHolder(refreshToken, accessToken);
    }
}
