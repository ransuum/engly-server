package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.response.ApiResponse;
import com.engly.engly_server.models.dto.response.UsersDto;
import com.engly.engly_server.service.common.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "10. User Management (Admin)", description = "APIs for administrators to manage user accounts.")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get a single user by ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "User found successfully.", content = @Content(schema = @Schema(implementation = UsersDto.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403", description = "Forbidden. User is not an administrator.", content = @Content)
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public UsersDto getUser(@PathVariable String id) {
        return userService.findById(id);
    }

    @Operation(summary = "Get a paginated list of all users")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "List of users retrieved successfully.", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403", description = "Forbidden. User is not an administrator.", content = @Content)
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public Page<UsersDto> getUsers(@ParameterObject
                                   @PageableDefault(sort = "username,asc") Pageable pageable) {
        return userService.allUsers(pageable);
    }

    @Operation(summary = "Delete a single user by ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204", description = "User deleted successfully.", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403", description = "Forbidden. User is not an administrator.", content = @Content)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.delete(id));
    }

    @DeleteMapping("/some")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Integer> deleteSomeUsers(@RequestParam List<String> ids) {
        return ResponseEntity.ok(userService.deleteSomeUsers(ids));
    }
}
