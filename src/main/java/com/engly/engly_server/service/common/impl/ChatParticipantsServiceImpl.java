package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.service.helper.ChatParticipantHelper;
import com.engly.engly_server.service.mapper.ChatParticipantMapper;
import com.engly.engly_server.models.dto.response.ChatParticipantsDto;
import com.engly.engly_server.models.entity.ChatParticipants;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.RoomRoles;
import com.engly.engly_server.repository.ChatParticipantRepository;
import com.engly.engly_server.service.common.ChatParticipantsService;
import com.engly.engly_server.utils.CacheName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.engly.engly_server.exception.handler.ExceptionMessage.PARTICIPANT_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatParticipantsServiceImpl implements ChatParticipantsService {

    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatParticipantHelper chatParticipantHelper;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.PARTICIPANTS_BY_ROOM, allEntries = true),
            @CacheEvict(value = CacheName.PARTICIPANT_EXISTS, key = "#roomId + '-' + #user.id")
    })
    public void addParticipant(String roomId, Users user, RoomRoles role) {
        if (!chatParticipantHelper.isParticipantExists(roomId, user.getId())) {
            final var chatParticipant = ChatParticipants.builder()
                    .roomId(roomId)
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
            @CacheEvict(value = CacheName.PARTICIPANT_EXISTS, allEntries = true),
            @CacheEvict(value = CacheName.COUNT_PARTICIPANTS, allEntries = true)
    })
    public void removeParticipant(String participantId) {
        chatParticipantRepository.findById(participantId)
                .ifPresentOrElse(chatParticipants -> {
                    chatParticipantRepository.deleteById(chatParticipants.getId());
                    log.info("User with email {} removed from room", chatParticipants.getUser().getEmail());
                }, () -> {
                    throw new NotFoundException(PARTICIPANT_NOT_FOUND.formatted(participantId));
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
                    throw new NotFoundException(PARTICIPANT_NOT_FOUND.formatted(participantId));
                });

    }

    @Override
    @Cacheable(value = CacheName.COUNT_PARTICIPANTS, key = "#roomId")
    public int countActiveParticipants(String roomId) {
        return chatParticipantRepository.countChatParticipantsByRoomId(roomId);
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
