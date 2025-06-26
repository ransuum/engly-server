package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.cache.ChatParticipantCache;
import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.mapper.ChatParticipantMapper;
import com.engly.engly_server.models.dto.ChatParticipantsDto;
import com.engly.engly_server.models.dto.create.ChatParticipantsRequestDto;
import com.engly.engly_server.models.entity.ChatParticipants;
import com.engly.engly_server.models.enums.Roles;
import com.engly.engly_server.repo.ChatParticipantRepo;
import com.engly.engly_server.service.common.ChatParticipantsService;
import com.engly.engly_server.utils.cache.CacheName;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ChatParticipantsServiceImpl implements ChatParticipantsService {
    private final ChatParticipantRepo chatParticipantRepo;
    private final ChatParticipantCache cache;

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheName.PARTICIPANTS_BY_ROOM, key = "#chatParticipantsRequestDto.rooms().id"),
            @CacheEvict(value = CacheName.PARTICIPANT_EXISTS, key = "#chatParticipantsRequestDto.rooms().id + '-' + #chatParticipantsRequestDto.user().id")
    })
    public void addParticipant(ChatParticipantsRequestDto chatParticipantsRequestDto) {
        if (!cache.isParticipantExists(chatParticipantsRequestDto.rooms().getId(), chatParticipantsRequestDto.user().getId())) {
            final var chatParticipant = ChatParticipants.builder()
                    .room(chatParticipantsRequestDto.rooms())
                    .user(chatParticipantsRequestDto.user())
                    .role(chatParticipantsRequestDto.role())
                    .build();
            log.info("User adds to room with email {}", chatParticipant.getUser().getEmail());
            chatParticipantRepo.save(chatParticipant);
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheName.PARTICIPANTS_BY_ROOM, key = "#result.room.id"),
            @CacheEvict(value = CacheName.PARTICIPANT_EXISTS, key = "#result.room.id + '-' + #result.user.id")
    })
    public void removeParticipant(String participantId) {
        final var chatParticipant = chatParticipantRepo.findById(participantId)
                .orElseThrow(() -> new NotFoundException("Participant with id " + participantId + " not found"));
        chatParticipantRepo.delete(chatParticipant);
    }

    @Override
    @CacheEvict(value = CacheName.PARTICIPANTS_BY_ROOM, key = "#result.room.id")
    public void updateRoleOfParticipant(String participantId, Roles role) {
        final var chatParticipant = chatParticipantRepo.findById(participantId)
                .orElseThrow(() -> new NotFoundException("Participant with id " + participantId + " not found"));
        chatParticipant.setRole(role);
        chatParticipantRepo.save(chatParticipant);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.PARTICIPANTS_BY_ROOM, key = "#roomId")
    public Page<ChatParticipantsDto> getParticipantsByRoomId(String roomId, Pageable pageable) {
        return chatParticipantRepo.findAllByRoom_Id(roomId, pageable).map(ChatParticipantMapper.INSTANCE::toDtoForRooms);
    }
}
