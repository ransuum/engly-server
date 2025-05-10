package com.engly.engly_server.service.notification;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.dto.EmailSendInfo;

public interface EmailVerificationService {
    EmailSendInfo sendMessage();
    AuthResponseDto checkToken(String token);
}
