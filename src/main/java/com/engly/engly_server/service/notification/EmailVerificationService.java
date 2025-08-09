package com.engly.engly_server.service.notification;

import com.engly.engly_server.models.dto.response.AuthResponseDto;
import com.engly.engly_server.models.dto.response.EmailSendInfo;
import jakarta.servlet.http.HttpServletResponse;

public interface EmailVerificationService {
    EmailSendInfo sendMessage();

    AuthResponseDto checkToken(String token, HttpServletResponse httpServletResponse);
}
