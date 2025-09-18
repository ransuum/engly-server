package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.request.MessageRequest;
import com.engly.engly_server.models.dto.response.UserWhoReadsMessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.concurrent.CompletableFuture;

public interface MessageReadService {
    CompletableFuture<Void> markMessageAsRead(MessageRequest messageRequest, String userId);

    Page<UserWhoReadsMessageDto> getUsersWhoReadMessage(String messageId, Pageable pageable);
}
