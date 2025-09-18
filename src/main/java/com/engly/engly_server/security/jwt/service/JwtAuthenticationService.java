package com.engly.engly_server.security.jwt.service;

import com.engly.engly_server.models.dto.request.AuthRequest;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.security.jwt.JwtHolder;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;

public interface JwtAuthenticationService {
    JwtHolder authentication(Users users, @NonNull HttpServletResponse response);

    void authenticationForGoogle(Users user, @NonNull HttpServletResponse response);

    JwtHolder authenticationWithParameters(Users users, @NonNull Authentication authentication, @NonNull HttpServletResponse response);

    Authentication authenticateCredentials(AuthRequest.SignInRequest sign);

    JwtHolder authenticationForVerification(Users users, @NonNull HttpServletResponse response);

    Authentication newAuthentication(Users users);
}
