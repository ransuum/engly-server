package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.request.GoogleUserInfoRequest;
import com.engly.engly_server.models.dto.response.AuthResponseDto;
import com.engly.engly_server.service.common.AdditionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/addition_info")
@Tag(name = "04. User Onboarding", description = "APIs for completing user registration, e.g., for social logins.")
public class AdditionalInfoController {
    private final AdditionalService additionalService;

    public AdditionalInfoController(AdditionalService additionalService) {
        this.additionalService = additionalService;
    }

    @Operation(
            summary = "Provide additional info for a Google user",
            description = """
                          Completes the registration process for a user who has authenticated via Google.
                          This endpoint is used to collect details like a unique username that are not provided by the social login.
                          
                          **Authorization:** Requires an authenticated user with the 'SCOPE_ADDITIONAL_INFO'. This scope is typically granted temporarily after a successful Google login but before this step is completed.
                          """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully registered. Returns new JWT tokens.",
                    content = { @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthResponseDto.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request. The provided data is invalid (e.g., username already taken, invalid format).",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. The JWT is missing, invalid, or expired.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden. The user does not have the required 'SCOPE_ADDITIONAL_INFO' scope.",
                    content = @Content
            )
    })
    @PreAuthorize("hasAuthority('SCOPE_ADDITIONAL_INFO')")
    @PostMapping("/for-google")
    public ResponseEntity<AuthResponseDto> addInfo(@RequestBody @Valid GoogleUserInfoRequest additionalRequestForGoogleUserDto,
                                                   HttpServletResponse httpServletResponse,
                                                   @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(201)
                .body(additionalService.additionalRegistration(jwt.getClaim("userId"), additionalRequestForGoogleUserDto, httpServletResponse));
    }
}
