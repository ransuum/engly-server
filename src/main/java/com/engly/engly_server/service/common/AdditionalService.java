package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.dto.create.GoogleUserInfoRequest;
import jakarta.servlet.http.HttpServletResponse;

@FunctionalInterface
public interface AdditionalService {
    AuthResponseDto additionalRegistration(GoogleUserInfoRequest additionalRequestForGoogleUserDto,
                                           HttpServletResponse httpServletResponse);
}
