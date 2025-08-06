package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.service.common.AuthService;
import com.engly.engly_server.utils.fieldvalidation.FieldUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleAuthorizationServiceImpl implements AuthenticationSuccessHandler {
    private final AuthService authService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        final var oauth2User = Optional.ofNullable(authentication.getPrincipal())
                .map(OAuth2User.class::cast)
                .orElseThrow(() -> new NotFoundException("Invalid OAuth2 response"));

        final String email = oauth2User.getAttribute("email");
        final String name = oauth2User.getAttribute("name");
        final String providerId = oauth2User.getAttribute("sub");
        FieldUtil.isValid(email, name, providerId);

        authService.processOAuth2PostLogin(email, name, providerId, response);

        final var builderUrl = UriComponentsBuilder.fromUriString(frontendUrl)
                .path("/google-auth/callback")
                .build().toUriString();
        response.sendRedirect(builderUrl);
    }
}
