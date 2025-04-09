package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.RoomsDto;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.models.request.create.RoomRequest;
import com.engly.engly_server.models.request.update.RoomUpdateRequest;
import com.engly.engly_server.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new room", description = "Creates a new room with the specified category and details")
    @PreAuthorize("hasAuthority('SCOPE_CREATE_GLOBAL')")
    public ResponseEntity<RoomsDto> createRoom(@RequestParam CategoryType name, @RequestBody RoomRequest roomRequest) {
        return new ResponseEntity<>(roomService.createRoom(name, roomRequest), HttpStatus.CREATED);
    }


    @GetMapping("/by-category")
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @Operation(summary = "Get rooms by category",
            description = """
                    Retrieves paginated list of rooms filtered by category \n
                    page starts from 0 \n
                    How to use? \n
                    You can use in 3 different ways \n
                    {
                      "page": 2,
                      "size": 20,
                      "sort": "id"
                    } \n
                    /////// \n
                    {
                       "page": 1,
                       "size": 10,
                       "sort": "id,ASC"
                     } \n
                     //// \n
                    {
                       "page": 0,
                       "size": 10,
                       "sort": "id,DESC"
                    } \n
                    /// \n
                    id can be replaced by different fields in RoomsDto
                    \s"""
    )
    public ResponseEntity<Page<RoomsDto>> getRoomsByCategory(
            @RequestParam CategoryType category,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(roomService.findAllRoomsByCategoryType(category, pageable));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a room", description = "Deletes a room by its ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('SCOPE_DELETE_GLOBAL')")
    public ResponseEntity<Void> deleteRoom(@PathVariable String id) {
        roomService.deleteRoomById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update room details", description = "Partially updates room information")
    @PreAuthorize("hasAuthority('SCOPE_UPDATE_GLOBAL')")
    public ResponseEntity<RoomsDto> updateRoom(
            @PathVariable String id,
            @Valid @RequestBody RoomUpdateRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }
}
