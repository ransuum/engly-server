package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.RoomsDto;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.models.request.create.RoomRequest;
import com.engly.engly_server.models.request.update.RoomUpdateRequest;
import com.engly.engly_server.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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
                    Retrieves paginated list of rooms filtered by category
                    page starts from 0
                    How to use?
                    You can use in 3 different ways
                    {
                      "page": 2,
                      "size": 20,
                      "sort": "id"
                    }
                    ///////
                    {
                       "page": 1,
                       "size": 10,
                       "sort": "id,ASC"
                     }
                     ////
                    {
                       "page": 0,
                       "size": 10,
                       "sort": "id,DESC"
                    }
                    ///
                    id can be replaced by different fields in RoomsDto
                    \s"""
    )
    public ResponseEntity<PagedModel<EntityModel<RoomsDto>>> getRoomsByCategory(
            @RequestParam CategoryType category,
            @PageableDefault Pageable pageable,
            PagedResourcesAssembler<RoomsDto> assembler) {
        var rooms = roomService.findAllRoomsByCategoryType(category, pageable);
        return ResponseEntity.ok(assembler.toModel(rooms));
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
