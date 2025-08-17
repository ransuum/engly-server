package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.response.AuthResponseDto;
import com.engly.engly_server.models.dto.request.GoogleUserInfoRequest;
import jakarta.servlet.http.HttpServletResponse;

@FunctionalInterface
public interface AdditionalService {
    AuthResponseDto additionalRegistration(String id, GoogleUserInfoRequest additionalRequest,
                                           HttpServletResponse httpServletResponse);
}
