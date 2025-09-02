package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.request.MessageRequest;
import com.engly.engly_server.models.dto.request.MessageSearchCriteriaRequest;
import com.engly.engly_server.models.dto.response.MessagesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {

    String NOT_FOUND_MESSAGE = "Cannot found this message";

    MessagesDto sendMessage(MessageRequest.CreateMessageRequest createMessageRequest);

    void deleteMessage(String id);

    MessagesDto findById(String id);

    MessagesDto editMessage(String id, String content);

    Page<MessagesDto> findAllMessageInCurrentRoomNative(String roomId, Pageable pageable);

    Page<MessagesDto> findMessagesByCriteria(MessageSearchCriteriaRequest request, Pageable pageable);
}
