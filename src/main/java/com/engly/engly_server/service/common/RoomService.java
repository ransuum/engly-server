package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.RoomsDto;
import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.models.dto.create.RoomRequestDto;
import com.engly.engly_server.models.dto.update.RoomUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomService {

    String ROOM_NOT_FOUND = "Room not found";
    String ROOM_ALREADY_EXISTS = "Room with this name already exists";

    RoomsDto createRoom(CategoryType name, RoomRequestDto roomRequestDto);

    void deleteRoomById(String id);

    RoomsDto updateRoom(String id, RoomUpdateRequest request);

    Page<RoomsDto> findAllRoomsByCategoryType(CategoryType category, Pageable pageable);

    Page<RoomsDto> findAllRoomsByCategoryTypeContainingKeyString(CategoryType categoryType, String keyString, Pageable pageable);

    Rooms findRoomEntityById(String id);
}
