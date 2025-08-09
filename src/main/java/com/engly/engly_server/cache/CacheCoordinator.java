package com.engly.engly_server.cache;

import com.engly.engly_server.cache.components.ChatParticipantCache;
import com.engly.engly_server.cache.components.MessageReadCache;

public sealed interface CacheCoordinator permits CacheCoordinatorImpl {

    MessageReadCache getMessageReadCache();

    ChatParticipantCache getChatParticipantCache();
}
