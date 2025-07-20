package com.engly.engly_server.cache;

import com.engly.engly_server.cache.components.ChatParticipantCache;
import com.engly.engly_server.cache.components.MessageReadCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class CachingServiceImpl implements CachingService {

    private final ChatParticipantCache chatParticipantCache;
    private final MessageReadCache messageReadCache;

    @Override
    public MessageReadCache getMessageReadCache() {
        return messageReadCache;
    }

    @Override
    public ChatParticipantCache getChatParticipantCache() {
        return chatParticipantCache;
    }
}
