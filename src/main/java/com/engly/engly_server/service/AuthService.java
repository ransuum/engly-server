package com.engly.engly_server.service;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.request.create.SignUpRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface AuthService {
    AuthResponseDto getJwtTokensAfterAuthentication(Authentication authentication, HttpServletResponse response);

    Object getAccessTokenUsingRefreshToken(String authorizationHeader);

    AuthResponseDto registerUser(SignUpRequest signUpRequest, HttpServletResponse httpServletResponse);

    Map<String, Boolean> checkUsernameAvailability(String username);

    Map<String, Boolean> checkEmailAvailability(String email);
}
