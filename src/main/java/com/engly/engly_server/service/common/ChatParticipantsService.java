package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.ChatParticipantsDto;
import com.engly.engly_server.models.dto.create.ChatParticipantsRequestDto;
import com.engly.engly_server.models.enums.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatParticipantsService {
    void addParticipant(ChatParticipantsRequestDto chatParticipantsRequestDto);

    void removeParticipant(String participantId);

    void updateRoleOfParticipant(String participantId, Roles role);

    Page<ChatParticipantsDto> getParticipantsByRoomId(String roomId, Pageable pageable);
}
