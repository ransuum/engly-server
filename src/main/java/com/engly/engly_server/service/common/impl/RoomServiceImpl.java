package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.mapper.RoomMapper;
import com.engly.engly_server.models.dto.ApiResponse;
import com.engly.engly_server.models.dto.RoomsDto;
import com.engly.engly_server.models.dto.create.RoomRequestDto;
import com.engly.engly_server.models.dto.update.RoomUpdateRequest;
import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.repo.RoomRepo;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.service.common.CategoriesService;
import com.engly.engly_server.service.common.RoomService;
import com.engly.engly_server.service.common.UserService;
import com.engly.engly_server.utils.cache.CacheName;
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

import static com.engly.engly_server.utils.fieldvalidation.FieldUtil.isValid;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepo roomRepo;
    private final UserService userService;
    private final CategoriesService categoriesRepo;
    private final SecurityService service;

    @Override
    @Caching(
            put = {
                    @CachePut(value = CacheName.ROOM_ID, key = "#result.id")
            }
    )
    @Transactional
    public RoomsDto createRoom(CategoryType name, RoomRequestDto roomRequestDto) {
        final var username = service.getCurrentUserEmail();
        final var category = categoriesRepo.findByName(name);
        final var creator = userService.findUserEntityByEmail(username);
        final var room = roomRepo.save(Rooms.builder()
                .creator(creator)
                .createdAt(Instant.now())
                .category(category)
                .description(roomRequestDto.description())
                .name(roomRequestDto.name())
                .build());
        return RoomMapper.INSTANCE.roomToDto(room);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheName.ROOM_ID, key = "#id"),
            @CacheEvict(value = CacheName.ROOM_ENTITY_ID, key = "#id")
    })
    public ApiResponse deleteRoomById(String id) {
        return roomRepo.findById(id)
                .map(rooms -> {
                    roomRepo.delete(rooms);
                    return new ApiResponse("Room deleted successfully", true, Instant.now());
                })
                .orElseThrow(() -> new NotFoundException("You can't delete this room"));
    }

    @Override
    @Caching(
            put = { @CachePut(value = CacheName.ROOM_ID, key = "#id") },
            evict = { @CacheEvict(value = CacheName.ROOM_ENTITY_ID, key = "#id") }
    )
    public RoomsDto updateRoom(String id, RoomUpdateRequest request) {
        return roomRepo.findById(id)
                .map(room -> {
                    if (isValid(request.newCategory()))
                        room.setCategory(categoriesRepo.findByName(request.newCategory()));

                    if (isValid(request.updateCreatorByEmail()))
                        room.setCreator(userService.findUserEntityByEmail(request.updateCreatorByEmail()));

                    if (isValid(request.description())) room.setDescription(request.description());
                    if (isValid(request.name())) room.setName(request.name());
                    return RoomMapper.INSTANCE.roomToDto(roomRepo.save(room));
                })
                .orElseThrow(() -> new NotFoundException("Room not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomsDto> findAllRoomsByCategoryType(CategoryType category, Pageable pageable) {
        return roomRepo.findAllByCategory_Name(category, pageable).map(RoomMapper.INSTANCE::roomToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomsDto> findAllRoomsContainingKeyString(String keyString, Pageable pageable) {
        return roomRepo.findAllRoomsContainingKeyString(keyString, pageable).map(RoomMapper.INSTANCE::roomToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomsDto> findAllRoomsByCategoryTypeContainingKeyString(CategoryType categoryType, String keyString, Pageable pageable) {
        return roomRepo.findAllByNameContainingIgnoreCaseAndCategoryName(keyString, categoryType, pageable)
                .map(RoomMapper.INSTANCE::roomToDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.ROOM_ENTITY_ID, key = "#id", sync = true)
    public Rooms findRoomEntityById(String id) {
        return roomRepo.findById(id).orElseThrow(() -> new NotFoundException("Room not found"));
    }
}
