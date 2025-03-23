package com.engly.engly_server.service;

import com.engly.engly_server.models.dto.RoomsDto;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.models.request.createrequests.RoomRequest;
import com.engly.engly_server.models.request.updaterequests.RoomUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomService {
    RoomsDto createRoom(CategoryType name, RoomRequest roomRequest);
    Page<RoomsDto> findAllRoomsByCategoryType(CategoryType category, Pageable pageable);
    void deleteRoomById(String id);
    RoomsDto updateRoom(String id, RoomUpdateRequest request);
}
