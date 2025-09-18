package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.response.AuthResponseDto;
import com.engly.engly_server.models.dto.request.GoogleUserInfoRequest;
import com.engly.engly_server.models.entity.AdditionalInfo;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.repository.UserRepository;
import com.engly.engly_server.security.jwt.service.JwtAuthenticationService;
import com.engly.engly_server.service.common.AdditionalService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdditionalServiceImpl implements AdditionalService {

    private final UserRepository userRepository;
    private final JwtAuthenticationService jwtAuthenticationService;

    private static final String ROLE_USER = "ROLE_USER";

    @Override
    @Transactional
    public AuthResponseDto additionalRegistration(String id, GoogleUserInfoRequest additionalRequest,
                                                  HttpServletResponse httpServletResponse) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setRoles(ROLE_USER);
                    user.setImgUrl(additionalRequest.imgUrl());
                    user.setAdditionalInfo(AdditionalInfo.builder()
                            .user(user)
                            .goal(additionalRequest.goals())
                            .nativeLanguage(additionalRequest.nativeLanguage())
                            .englishLevel(additionalRequest.englishLevel())
                            .build());
                    final var savedUser = userRepository.save(user);

                    final var authentication = jwtAuthenticationService.newAuthentication(savedUser);
                    final var jwtHolder = jwtAuthenticationService.authenticationWithParameters(savedUser, authentication, httpServletResponse);

                    return new AuthResponseDto(jwtHolder.accessToken(), 300, TokenType.BEARER, savedUser.getUsername());
                })
                .orElseThrow(() -> new NotFoundException("Invalid User"));
    }
}
