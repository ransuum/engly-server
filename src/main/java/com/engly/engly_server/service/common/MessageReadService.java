package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.UserWhoReadsMessageDto;

import java.util.List;

public interface MessageReadService {
    void markMessageAsRead(String messageId, String userId);

    List<UserWhoReadsMessageDto> getUsersWhoReadMessage(String messageId);

    boolean hasUserReadMessage(String messageId, String userId);
}
