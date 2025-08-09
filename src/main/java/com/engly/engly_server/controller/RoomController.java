package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.response.RoomsDto;
import com.engly.engly_server.models.dto.request.RoomRequest;
import com.engly.engly_server.models.dto.request.RoomUpdateRequest;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.service.common.RoomService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "09. Rooms", description = "APIs for creating, managing, and searching for chat rooms.")
@SecurityRequirement(name = "bearerAuth")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @Operation(
            summary = "Create a new room",
            description = "Creates a new chat room with the specified details and assigns it to a category."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Room created successfully.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RoomsDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid room data provided.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden. User does not have 'SCOPE_CREATE_GLOBAL'.", content = @Content)
    })
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @RateLimiter(name = "RoomController")
    public ResponseEntity<RoomsDto> createRoom(
            @Parameter(description = "The category to assign the new room to.", required = true)
            @RequestParam CategoryType name,
            @Valid @RequestBody RoomRequest roomRequestDto) {
        return ResponseEntity.status(201).body(roomService.createRoom(name, roomRequestDto));
    }


    @Operation(
            summary = "Get a paginated list of rooms",
            description = """
                          Retrieves a paginated list of rooms.
                          This endpoint supports optional filtering by category and searching by a query string.
                          
                          - To filter by category, use the `category` parameter (e.g., `?category=NEWS`).
                          """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of rooms retrieved successfully.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden. User does not have 'SCOPE_READ'.", content = @Content)
    })
    @GetMapping("/by-category")
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    public ResponseEntity<Page<RoomsDto>> getRoomsByCategory(
            @Parameter(description = "Filter rooms by a specific category.")
            @RequestParam(defaultValue = "NEWS") CategoryType category,
            @ParameterObject @PageableDefault(page = 0, size = 8,
                    sort = {"name"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(roomService.findAllRoomsByCategoryType(category, pageable));
    }

    @GetMapping("/find/in/{category}/by-keyString/")
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @Operation(summary = "Find appropriate rooms by keyString and category",
            description = """
                    Retrieves paginated list of rooms filtered by category, name
                    Searching is done ignore case of keyString and fields
                    page starts from 0
                    keyString is a string that needs to be found in name
                    id can be replaced by different fields in RoomsDto
                    \s"""
    )
    @RateLimiter(name = "RoomController")
    public ResponseEntity<Page<RoomsDto>> findRoomsByCategoryAndKeyString(
            @PathVariable(value = "category") CategoryType category,
            @RequestParam(value = "keyString") String keyString,
            @ParameterObject @PageableDefault(page = 0, size = 8,
                    sort = {"name"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(roomService.findAllRoomsByCategoryTypeContainingKeyString(category, keyString, pageable));
    }

    @Operation(summary = "Delete a room by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Room deleted successfully.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden. User does not have 'SCOPE_DELETE_GLOBAL'.", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_DELETE_GLOBAL')")
    @RateLimiter(name = "RoomController")
    public ResponseEntity<Void> deleteRoom(@PathVariable String id) {
        roomService.deleteRoomById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update room details", description = "Partially updates room information")
    @PreAuthorize("hasAuthority('SCOPE_UPDATE_GLOBAL')")
    @RateLimiter(name = "RoomController")
    public ResponseEntity<RoomsDto> updateRoom(
            @PathVariable String id,
            @Valid @RequestBody RoomUpdateRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }
}
