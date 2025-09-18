package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.request.RoomRequest;
import com.engly.engly_server.models.dto.request.RoomSearchCriteriaRequest;
import com.engly.engly_server.models.dto.response.RoomsDto;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.service.common.RoomService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
    @ApiResponse(responseCode = "201", description = "Room created successfully.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RoomsDto.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request. Invalid room data provided.", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden. User does not have 'SCOPE_CREATE_GLOBAL'.", content = @Content)
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @RateLimiter(name = "RoomController")
    public ResponseEntity<RoomsDto> createRoom(
            @Parameter(description = "The category to assign the new room to.", required = true)
            @RequestParam CategoryType name,
            @RequestBody @Valid RoomRequest.RoomCreateRequest roomCreateRequestDto,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(201).body(roomService.createRoom(jwt.getClaim("userId"), name, roomCreateRequestDto));
    }


    @Operation(
            summary = "Get a paginated list of rooms",
            description = """
                    Retrieves a paginated list of rooms.
                    This endpoint supports optional filtering by category and searching by a query string.
                    
                    - To filter by category, use the `category` parameter (e.g., `?category=NEWS`).
                    """
    )
    @ApiResponse(responseCode = "200", description = "List of rooms retrieved successfully.", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden. User does not have 'SCOPE_READ'.", content = @Content)
    @GetMapping("/by-category")
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    public ResponseEntity<Page<RoomsDto>> getRoomsByCategory(
            @Parameter(description = "Filter rooms by a specific category.")
            @RequestParam(defaultValue = "NEWS") CategoryType category,
            @ParameterObject @PageableDefault(size = 8, sort = {"name"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(roomService.findAllRoomsByCategoryType(category, pageable));
    }

    @Operation(summary = "Delete a room by its ID")
    @ApiResponse(responseCode = "204", description = "Room deleted successfully.", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden. User does not have 'SCOPE_DELETE_GLOBAL'.", content = @Content)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_DELETE_GLOBAL')")
    @RateLimiter(name = "RoomController")
    public ResponseEntity<Void> deleteRoom(@PathVariable String id) {
        roomService.deleteRoomById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get rooms by criteria")
    @ApiResponse(responseCode = "200", description = "Page with rooms displays successfully.", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden. User does not have 'SCOPE_READ'.", content = @Content)
    @GetMapping("/by-criteria")
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    public Page<RoomsDto> findRoomsByCriteria(@ModelAttribute RoomSearchCriteriaRequest request,
                                              @ParameterObject @PageableDefault(size = 8, sort = {"name"},
                                                      direction = Sort.Direction.ASC) Pageable pageable) {
        return roomService.findAllWithCriteria(request, pageable);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update room details", description = "Partially updates room information")
    @PreAuthorize("hasAuthority('SCOPE_UPDATE_GLOBAL')")
    @RateLimiter(name = "RoomController")
    public RoomsDto updateRoom(@PathVariable String id,
                               @RequestBody RoomRequest.RoomUpdateRequest request) {
        return roomService.updateRoom(id, request);
    }
}
