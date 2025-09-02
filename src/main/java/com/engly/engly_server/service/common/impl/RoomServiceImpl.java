package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.EntityAlreadyExistsException;
import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.mapper.RoomMapper;
import com.engly.engly_server.models.dto.request.RoomRequest;
import com.engly.engly_server.models.dto.request.RoomSearchCriteriaRequest;
import com.engly.engly_server.models.dto.response.RoomsDto;
import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.models.enums.RoomRoles;
import com.engly.engly_server.repository.RoomRepository;
import com.engly.engly_server.service.common.CategoriesService;
import com.engly.engly_server.service.common.ChatParticipantsService;
import com.engly.engly_server.service.common.RoomService;
import com.engly.engly_server.service.common.UserService;
import com.engly.engly_server.utils.cache.CacheName;
import com.engly.engly_server.utils.fieldvalidation.FieldUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final UserService userService;
    private final CategoriesService categoriesService;
    private final ChatParticipantsService chatParticipantsService;

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = CacheName.ROOMS_BY_CATEGORY, allEntries = true),
                    @CacheEvict(value = CacheName.ROOMS_BY_CRITERIA, allEntries = true)
            }
    )
    public RoomsDto createRoom(String id, CategoryType name, RoomRequest.RoomCreateRequest roomCreateRequestDto) {
        if (roomRepository.existsByName(roomCreateRequestDto.name()))
            throw new EntityAlreadyExistsException(ROOM_ALREADY_EXISTS);

        final var room = Rooms.builder()
                .creator(userService.findEntityById(id))
                .createdAt(Instant.now())
                .category(categoriesService.findByName(name))
                .description(roomCreateRequestDto.description())
                .name(roomCreateRequestDto.name())
                .build();

        chatParticipantsService.addParticipant(roomRepository.save(room), room.getCreator(), RoomRoles.ADMIN);
        return RoomMapper.INSTANCE.roomToDto(room);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheName.ROOMS_BY_CRITERIA,
            key = "#request.hashCode() + ':' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort.toString()",
            condition = "#pageable.pageNumber < 3 && #pageable.pageSize <= 20",
            unless = "#result.content.isEmpty()"
    )
    public Page<RoomsDto> findAllWithCriteria(RoomSearchCriteriaRequest request, Pageable pageable) {
        return roomRepository.findAll(request.buildSpecification(), pageable).map(RoomMapper.INSTANCE::roomToDto);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.ROOM_ENTITY_ID, key = "#id"),
            @CacheEvict(value = CacheName.ROOMS_BY_CATEGORY, allEntries = true),
            @CacheEvict(value = CacheName.ROOMS_BY_CRITERIA, allEntries = true)
    })
    public void deleteRoomById(String id) {
        roomRepository.findById(id).ifPresentOrElse(room -> roomRepository.deleteById(room.getId()),
                () -> {
                    throw new NotFoundException(ROOM_NOT_FOUND);
                });
    }

    @Override
    @Caching(
            put = {@CachePut(value = CacheName.ROOM_DTO_ID, key = "#id")},
            evict = {
                    @CacheEvict(value = CacheName.ROOMS_BY_CATEGORY, allEntries = true),
                    @CacheEvict(value = CacheName.ROOMS_BY_CRITERIA, allEntries = true)
            }
    )
    public RoomsDto updateRoom(String id, RoomRequest.RoomUpdateRequest request) {
        return roomRepository.findById(id)
                .map(room -> {
                    if (FieldUtil.isValid(request.newCategory()))
                        room.setCategory(categoriesService.findByName(request.newCategory()));

                    if (isNotBlank(request.updateCreatorByEmail()))
                        room.setCreator(userService.findUserEntityByEmail(request.updateCreatorByEmail()));

                    if (isNotBlank(request.description())) room.setDescription(request.description());
                    if (isNotBlank(request.name())) room.setName(request.name());
                    return RoomMapper.INSTANCE.roomToDto(roomRepository.save(room));
                })
                .orElseThrow(() -> new NotFoundException(ROOM_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheName.ROOMS_BY_CATEGORY,
            key = "#category.name() + ':native:' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort.toString()",
            condition = "#pageable.pageNumber < 3 && #pageable.pageSize <= 20",
            unless = "#result.content.isEmpty()"
    )
    public Page<RoomsDto> findAllRoomsByCategoryType(CategoryType category, Pageable pageable) {
        return roomRepository.findByCategoryName(category, pageable).map(RoomMapper.INSTANCE::roomToDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.ROOM_ENTITY_ID, key = "#id", sync = true)
    public Rooms findRoomEntityById(String id) {
        return roomRepository.findById(id).orElseThrow(() -> new NotFoundException(ROOM_NOT_FOUND));
    }
}
