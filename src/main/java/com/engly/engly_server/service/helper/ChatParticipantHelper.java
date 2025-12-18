package com.engly.engly_server.service.helper;

import com.engly.engly_server.repository.ChatParticipantRepository;
import com.engly.engly_server.utils.CacheName;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatParticipantHelper {
    private final ChatParticipantRepository chatParticipantRepository;

    @Cacheable(value = CacheName.PARTICIPANT_EXISTS,
            key = "#roomId + '-' + #userId",
            condition = "#userId != null && #roomId != null")
    public boolean isParticipantExists(String roomId, String userId) {
        return chatParticipantRepository.existsByRoomIdAndUserId(roomId, userId);
    }
}
