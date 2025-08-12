package com.engly.engly_server.security.jwt.service;

import com.engly.engly_server.models.dto.request.SignInRequest;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.security.jwt.JwtHolder;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public interface JwtAuthenticationService {
    JwtHolder authentication(Users users, HttpServletResponse response);

    void authenticationForGoogle(Users user, HttpServletResponse response);

    JwtHolder authenticationWithParameters(Users users, Authentication authentication, HttpServletResponse response);

    Authentication authenticateCredentials(SignInRequest sign);

    JwtHolder authenticationForVerification(Users users, HttpServletResponse response);

    Authentication newAuthentication(Users users);
}
