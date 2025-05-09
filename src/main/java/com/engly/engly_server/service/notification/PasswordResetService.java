package com.engly.engly_server.service.notification;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.dto.EmailSendInfo;
import com.engly.engly_server.models.dto.update.PasswordResetRequest;

public interface PasswordResetService {
    EmailSendInfo sendMessage(String email);

    AuthResponseDto passwordReset(PasswordResetRequest data);
}
