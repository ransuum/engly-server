package com.engly.engly_server.service;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.dto.create.SignUpRequestDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {
    AuthResponseDto getJwtTokensAfterAuthentication(Authentication authentication, HttpServletResponse response);

    Object getAccessTokenUsingRefreshToken(String authorizationHeader);

    AuthResponseDto registerUser(SignUpRequestDto signUpRequestDto, HttpServletResponse httpServletResponse);
}
