package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.request.MessageRequest;
import com.engly.engly_server.models.dto.request.MessageSearchCriteriaRequest;
import com.engly.engly_server.models.dto.response.MessagesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {

    MessagesDto sendMessage(MessageRequest createMessageRequest);

    void deleteMessage(MessageRequest deleteRequest);

    MessagesDto findById(String id);

    MessagesDto editMessage(MessageRequest editRequest);

    Page<MessagesDto> findAllMessageInCurrentRoomNative(String roomId, Pageable pageable);

    Page<MessagesDto> findMessagesByCriteria(MessageSearchCriteriaRequest request, Pageable pageable);
}
