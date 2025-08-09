package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.response.AvailabilityResponseDto;
import com.engly.engly_server.models.dto.response.GoogleAvailabilityDto;
import com.engly.engly_server.service.common.UserValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/valid")
@RequiredArgsConstructor
@Tag(name = "11. User validation", description = "APIs for user validation operations.")
public class UserValidationController {
    private final UserValidationService userValidationService;

    @Operation(
            summary = "Check Google User's First Login Status",
            description = "Checks if the currently authenticated Google user needs to provide additional information (e.g., set a username). " +
                    "This helps the frontend decide whether to redirect the user to the 'complete profile' page after login.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Status retrieved successfully.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GoogleAvailabilityDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized. User is not authenticated.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden. User does not have 'SCOPE_AUTHORIZE'.", content = @Content)
    })
    @GetMapping("/first-login")
    @PreAuthorize("hasAuthority('SCOPE_AUTHORIZE')")
    public ResponseEntity<GoogleAvailabilityDto> firstLogin() {
        return ResponseEntity.ok(userValidationService.firstLogin());
    }

    @Operation(
            summary = "Перевірка при реєстрації на username live",
            description = """
                        Дозволяє перевіряти в живую юзернейм - це для фронтенду, треба додати listener
                    """
    )
    @GetMapping("/check-username")
    public ResponseEntity<AvailabilityResponseDto> checkUsernameAvailability(
            @NotBlank(message = "Username is blank")
            @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters.")
            @Pattern(
                    regexp = "^[a-zA-Z]{2,50}$",
                    message = "Username must contain only letters (a-z, A-Z) and be between 2 and 50 characters long."
            )
            @RequestParam
            String username) {
        return ResponseEntity.ok(userValidationService.isUsernameAvailable(username));
    }

    @Operation(
            summary = "Check if an email is available",
            description = """
                          Performs a real-time check to see if a given email address is already registered.
                          This is intended for use on a sign-up form.
                          
                          Returns a boolean `isAvailable` field.
                          """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Check completed successfully.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AvailabilityResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request. The provided email is missing or is not a valid email format.",
                    content = @Content
            )
    })
    @GetMapping("/check-email")
    public ResponseEntity<AvailabilityResponseDto> checkEmailAvailability(
            @RequestParam
            @Email(message = "Isn't email")
            @NotBlank(message = "Email is blank")
            @Size(max = 50, message = "Email cannot exceed 50 characters. Please shorten your input.")
            String email) {
        return ResponseEntity.ok(userValidationService.isEmailAvailable(email));
    }
}
