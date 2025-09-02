package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.cache.CacheCoordinator;
import com.engly.engly_server.cache.components.ChatParticipantCache;
import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.mapper.ChatParticipantMapper;
import com.engly.engly_server.models.dto.response.ChatParticipantsDto;
import com.engly.engly_server.models.entity.ChatParticipants;
import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.RoomRoles;
import com.engly.engly_server.repository.ChatParticipantRepository;
import com.engly.engly_server.service.common.ChatParticipantsService;
import com.engly.engly_server.utils.cache.CacheName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ChatParticipantsServiceImpl implements ChatParticipantsService {

    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatParticipantCache chatParticipantCache;

    public ChatParticipantsServiceImpl(ChatParticipantRepository chatParticipantRepository,
                                       CacheCoordinator chatParticipantCache) {
        this.chatParticipantRepository = chatParticipantRepository;
        this.chatParticipantCache = chatParticipantCache.getChatParticipantCache();
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.PARTICIPANTS_BY_ROOM, allEntries = true),
            @CacheEvict(value = CacheName.PARTICIPANT_EXISTS, key = "#rooms.id + '-' + #user.id")
    })
    public void addParticipant(Rooms rooms, Users user, RoomRoles role) {
        if (!chatParticipantCache.isParticipantExists(rooms.getId(), user.getId())) {
            final var chatParticipant = ChatParticipants.builder()
                    .room(rooms)
                    .user(user)
                    .role(role)
                    .build();
            log.info("User adds to room with email {}", chatParticipant.getUser().getEmail());
            chatParticipantRepository.save(chatParticipant);
        }
    }

    @Override
    @Transactional(timeout = 30)
    @Caching(evict = {
            @CacheEvict(value = CacheName.PARTICIPANTS_BY_ROOM, allEntries = true),
            @CacheEvict(value = CacheName.PARTICIPANT_EXISTS, allEntries = true)
    })
    public void removeParticipant(String participantId) {
        chatParticipantRepository.findById(participantId)
                .ifPresentOrElse(chatParticipants -> {
                    chatParticipantRepository.deleteById(chatParticipants.getId());
                    log.info("User with email {} removed from room", chatParticipants.getUser().getEmail());
                }, () -> {
                    throw new NotFoundException(NOT_FOUND_MESSAGE.formatted(participantId));
                });
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.PARTICIPANTS_BY_ROOM, allEntries = true),
            @CacheEvict(value = CacheName.PARTICIPANT_EXISTS, key = "#result.room.id + '-' + #result.user.id", condition = "#result != null")
    })
    public void updateRoleOfParticipant(String participantId, RoomRoles role) {
        chatParticipantRepository.findById(participantId)
                .ifPresentOrElse(chatParticipants -> {
                    chatParticipants.setRole(role);
                    chatParticipantRepository.save(chatParticipants);
                }, () -> {
                    throw new NotFoundException(NOT_FOUND_MESSAGE.formatted(participantId));
                });

    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheName.PARTICIPANTS_BY_ROOM,
            key = "#roomId + ':native:' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort.toString()",
            condition = "#pageable.pageNumber < 5 && #pageable.pageSize <= 20",
            unless = "#result.content.isEmpty()"
    )
    public Page<ChatParticipantsDto> getParticipantsByRoomId(String roomId, Pageable pageable) {
        return chatParticipantRepository.findAllByRoomId(roomId, pageable)
                .map(ChatParticipantMapper.INSTANCE::toDtoForRooms);
    }
}
