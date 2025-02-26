package com.engly.engly_server.service;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.dto.EmailSendInfo;

public interface NotificationService {
    EmailSendInfo sendNotifyMessage();

    AuthResponseDto checkToken(String token);

}
