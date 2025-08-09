package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.response.UserWhoReadsMessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageReadService {
    void markMessageAsRead(List<String> messageIds, String userId);

    Page<UserWhoReadsMessageDto> getUsersWhoReadMessage(String messageId, Pageable pageable);
}
