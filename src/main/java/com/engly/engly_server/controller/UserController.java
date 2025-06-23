package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.ApiResponse;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.service.common.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found successfully.", content = @Content(schema = @Schema(implementation = UsersDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden. User is not an administrator.", content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<UsersDto> getUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(summary = "Get a paginated list of all users")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of users retrieved successfully.", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden. User is not an administrator.", content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<PagedModel<EntityModel<UsersDto>>> getUsers(
            @ParameterObject @PageableDefault(sort = "username,asc") Pageable pageable,
            PagedResourcesAssembler<UsersDto> assembler
    ) {
        final var users = userService.allUsers(pageable);
        return ResponseEntity.ok(assembler.toModel(users));
    }

    @Operation(summary = "Delete a single user by ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "User deleted successfully.", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden. User is not an administrator.", content = @Content),
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.delete(id));
    }

    @DeleteMapping("/some")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<PagedModel<EntityModel<UsersDto>>> deleteSomeUsers(
            @ParameterObject @PageableDefault(sort = "username,asc", size = 25, page = 5) Pageable pageable,
            PagedResourcesAssembler<UsersDto> assembler,
            @Parameter(
                    description = "A comma-separated list of user IDs to delete.",
                    required = true,
                    array = @ArraySchema(schema = @Schema(type = "string", example = "uuid1,uuid2,uuid3"))
            )
            @RequestParam List<String> ids
    ) {
        final var users = userService.deleteSomeUsers(ids);
        return ResponseEntity.ok(assembler.toModel(new PageImpl<>(users, pageable, users.size())));
    }
}
