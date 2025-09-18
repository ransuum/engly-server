package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.response.UsersDto;
import com.engly.engly_server.models.dto.request.ProfileUpdateRequest;
import com.engly.engly_server.service.common.ProfileService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "07. User Profile", description = "APIs for managing the authenticated user's profile.")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Operation(
            summary = "Get the current user's profile",
            description = "Retrieves the complete profile information for the currently authenticated user."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Profile data retrieved successfully.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UsersDto.class)
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized. User is not authenticated.",
            content = @Content
    )
    @ApiResponse(
            responseCode = "403",
            description = "Forbidden. User does not have the required 'SCOPE_AUTHORIZE' scope.",
            content = @Content
    )
    @GetMapping("/check")
    @PreAuthorize("hasAuthority('SCOPE_AUTHORIZE')")
    public UsersDto getProfile(@AuthenticationPrincipal Jwt jwt) {
        return profileService.getProfile(jwt.getClaim("userId"));
    }

    @Operation(
            summary = "Update the current user's profile",
            description = """
                    Updates specific fields of the currently authenticated user's profile.
                    
                    Only the fields provided in the request body will be updated. Null or omitted fields will be ignored.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Profile updated successfully. Returns the updated profile.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UsersDto.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad Request. The provided data is invalid (e.g., invalid email format, username already taken).",
            content = @Content
    )
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized. User is not authenticated.",
            content = @Content
    )
    @ApiResponse(
            responseCode = "403",
            description = "Forbidden. User does not have the required 'SCOPE_WRITE' scope.",
            content = @Content
    )
    @PatchMapping
    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @RateLimiter(name = "ProfileController")
    public ResponseEntity<UsersDto> updateProfile(@AuthenticationPrincipal Jwt jwt,
                                                  @RequestBody ProfileUpdateRequest profileUpdateData) {
        return ResponseEntity.ok(profileService.updateProfile(jwt.getClaim("userId"), profileUpdateData));
    }
}
