package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.dto.LoginGoogleResult;
import com.engly.engly_server.models.dto.create.SignInRequest;
import com.engly.engly_server.models.dto.create.SignUpRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    AuthResponseDto getJwtTokensAfterAuthentication(SignInRequest sign, HttpServletResponse response);

    AuthResponseDto getAccessTokenUsingRefreshToken(String refreshToken, HttpServletResponse response);

    AuthResponseDto registerUser(SignUpRequest signUpRequestDto, HttpServletResponse httpServletResponse);

    LoginGoogleResult processOAuth2PostLogin(String email, String name, String providerId, HttpServletResponse response);
}
