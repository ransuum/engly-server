package com.engly.engly_server.security.jwt.service;

import com.engly.engly_server.models.dto.request.AuthRequest;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.security.jwt.JwtHolder;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;

@NullMarked
public interface JwtAuthenticationService {
    @Nullable JwtHolder authentication(Users users, HttpServletResponse response);

    void authenticationForGoogle(Users user, HttpServletResponse response);

    @Nullable JwtHolder authenticationWithParameters(Users users, Authentication authentication, HttpServletResponse response);

    Authentication authenticateCredentials(AuthRequest.SignInRequest sign);

    @Nullable JwtHolder authenticationForVerification(Users users, HttpServletResponse response);

    Authentication newAuthentication(Users users);
}
