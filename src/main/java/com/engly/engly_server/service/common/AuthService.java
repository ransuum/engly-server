package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.response.AuthResponseDto;
import com.engly.engly_server.models.dto.request.SignInRequest;
import com.engly.engly_server.models.dto.request.SignUpRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    AuthResponseDto getJwtTokensAfterAuthentication(SignInRequest sign, HttpServletResponse response);

    AuthResponseDto getAccessTokenUsingRefreshToken(String refreshToken, HttpServletResponse response);

    AuthResponseDto registerUser(SignUpRequest signUpRequestDto, HttpServletResponse httpServletResponse);

    void processOAuth2PostLogin(String email, String name, String providerId, HttpServletResponse response);
}
