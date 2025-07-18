package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.builder.GoogleRedirectUrl;
import com.engly.engly_server.models.dto.create.SignUpRequestDto;
import com.engly.engly_server.models.enums.EnglishLevels;
import com.engly.engly_server.models.enums.Goals;
import com.engly.engly_server.models.enums.NativeLanguage;
import com.engly.engly_server.models.enums.Provider;
import com.engly.engly_server.security.jwt.service.JwtAuthenticationService;
import com.engly.engly_server.security.registration.RegistrationChooser;
import com.engly.engly_server.service.common.UserService;
import com.engly.engly_server.utils.fieldvalidation.FieldUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GoogleAuthorizationServiceImpl implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final Map<Provider, RegistrationChooser> chooserMap;
    private final JwtAuthenticationService jwtAuthenticationService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public GoogleAuthorizationServiceImpl(UserService userService, List<RegistrationChooser> choosers,
                                          JwtAuthenticationService jwtAuthenticationService) {
        this.userService = userService;
        this.chooserMap = choosers.stream()
                .collect(Collectors.toMap(RegistrationChooser::getProvider, o -> o));
        this.jwtAuthenticationService = jwtAuthenticationService;
    }

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

        final var user = userService.findByEmail(email)
                .map(existingUser -> {
                    existingUser.setLastLogin(Instant.now());
                    return existingUser;
                })
                .orElseGet(() -> chooserMap.get(Provider.GOOGLE)
                        .registration(new SignUpRequestDto(name, email, "Password123@",
                                EnglishLevels.A1, NativeLanguage.ENGLISH, Goals.DEFAULT, providerId)));

        final var jwtHolder = jwtAuthenticationService.createAuthObject(user, response);
        log.info("[AuthService:onAuthenticationSuccess] User:{} successfully authenticated with Google", user.getUsername());
        response.sendRedirect(new GoogleRedirectUrl(frontendUrl, jwtHolder.accessToken(),
                jwtHolder.refreshToken(), user.getUsername()).getRedirectUrl());
    }
}
