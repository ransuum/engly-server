package com.engly.engly_server.service.notification;

import com.engly.engly_server.models.dto.response.AuthResponseDto;
import com.engly.engly_server.models.dto.response.EmailSendInfo;
import com.engly.engly_server.models.dto.request.PasswordResetRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface PasswordResetService {
    EmailSendInfo sendMessage(String email);

    AuthResponseDto passwordReset(PasswordResetRequest data, HttpServletResponse httpServletResponse);
}
