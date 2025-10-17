package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.request.RoomRequest;
import com.engly.engly_server.models.dto.request.RoomSearchCriteriaRequest;
import com.engly.engly_server.models.dto.response.RoomDtoShort;
import com.engly.engly_server.models.dto.response.RoomsDto;
import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.enums.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomService {

    RoomsDto createRoom(String id, CategoryType name, RoomRequest.RoomCreateRequest roomCreateRequestDto);

    Page<RoomsDto> findAllWithCriteria(RoomSearchCriteriaRequest request, Pageable pageable);

    void deleteRoomById(String id);

    RoomsDto updateRoom(String id, RoomRequest.RoomUpdateRequest request);

    Page<RoomsDto> findAllRoomsByCategoryType(CategoryType category, Pageable pageable);

    Rooms findRoomEntityById(String id);

    RoomDtoShort findRoomByIdShort(String id);
}
