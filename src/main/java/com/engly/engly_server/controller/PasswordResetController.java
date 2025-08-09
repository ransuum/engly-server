package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.response.AuthResponseDto;
import com.engly.engly_server.models.dto.response.EmailSendInfo;
import com.engly.engly_server.models.dto.request.PasswordResetRequest;
import com.engly.engly_server.service.notification.PasswordResetService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password-reset")
@Slf4j
@Tag(name = "03. Password Recovery", description = "APIs for user password recovery.")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }


    @Operation(
            summary = "Request a password reset email",
            description = """
                          Initiates the password reset process for a user.
                          
                          Provide a registered email address, and if it exists, an email containing a unique password reset token will be sent.
                          
                          **Note:** For security reasons (to prevent email enumeration attacks), this endpoint will always return a successful `202 Accepted` response, regardless of whether the email address was found in the system.
                          """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "Request accepted. If the email exists, a reset link will be sent.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request. The provided email address is not in a valid format.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too Many Requests. Please wait before trying again.",
                    content = @Content
            )
    })
    @PostMapping("/send")
    @RateLimiter(name = "PasswordResetController")
    public ResponseEntity<EmailSendInfo> notifyUserPasswordReset(
            @Valid
            @Email(message = "Isn't email")
            @NotBlank(message = "Email is blank")
            @Size(max = 50, message = "Email cannot exceed 50 characters. Please shorten your input.")
            @RequestParam
            String email) {
        return ResponseEntity.status(202).body(passwordResetService.sendMessage(email));
    }

    @Operation(
            summary = "Execute a password reset",
            description = """
                          Sets a new password for a user using a valid token received via email.
                          
                          Upon successful password change, new authentication and refresh tokens are returned to allow the user to log in immediately with their new credentials.
                          """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password successfully reset. Returns new authentication tokens.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuthResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request. The request is invalid due to one of the following: " +
                            "1. The reset token is invalid or has expired. " +
                            "2. The new password does not meet the security policy (e.g., too short, too common).",
                    content = @Content
            )
    })
    @PostMapping
    @RateLimiter(name = "PasswordResetController")
    public ResponseEntity<AuthResponseDto> passwordReset(@Valid @RequestBody PasswordResetRequest data, HttpServletResponse response) {
        return ResponseEntity.ok(passwordResetService.passwordReset(data, response));
    }
}
