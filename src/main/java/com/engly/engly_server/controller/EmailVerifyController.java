package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.response.AuthResponseDto;
import com.engly.engly_server.models.dto.response.EmailSendInfo;
import com.engly.engly_server.service.notification.EmailVerificationService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email-verify")
@Tag(name = "02. Email Verification", description = "APIs for verifying a user's email address after registration.")
@SecurityRequirement(name = "bearerAuth")
public class EmailVerifyController {

    private final EmailVerificationService emailVerificationService;

    public EmailVerifyController(EmailVerificationService emailVerificationService) {
        this.emailVerificationService = emailVerificationService;
    }

    @Operation(
            summary = "Request a verification email",
            description = """
                    Triggers an email to be sent to the authenticated user's registered email address.
                    The email contains a unique verification token.
                    
                    **Authorization:** This endpoint is only accessible to users who have not yet verified their email (i.e., those with `SCOPE_NOT_VERIFIED`).
                    """
    )
    @ApiResponse(
            responseCode = "202",
            description = "Request accepted. An email will be sent shortly.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EmailSendInfo.class))
    )
    @ApiResponse(
            responseCode = "403",
            description = "Forbidden. The user is already verified or does not have the 'SCOPE_NOT_VERIFIED'.",
            content = @Content
    )
    @ApiResponse(
            responseCode = "429",
            description = "Too Many Requests. The user has requested emails too frequently.",
            content = @Content
    )
    @PreAuthorize("hasAuthority('SCOPE_NOT_VERIFIED')")
    @PostMapping
    @RateLimiter(name = "EmailVerifyController")
    public ResponseEntity<EmailSendInfo> notifyUserEmailVerify(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(202).body(emailVerificationService.sendMessage(jwt.getSubject()));
    }

    @Operation(
            summary = "Verify an email address using a token",
            description = """
                    Confirms a user's email address using the token sent to them.
                    The token is provided as a query parameter in the verification link.
                    
                    Upon successful verification, the user's roles are updated (e.g., `SCOPE_NOT_VERIFIED` is removed and `SCOPE_READ`/`SCOPE_WRITE` are added), and a new set of JWTs is issued.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Email successfully verified. Returns new JWT tokens.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuthResponseDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad Request. The token is invalid, expired, or does not match the user.",
            content = @Content
    )
    @ApiResponse(
            responseCode = "403",
            description = "Forbidden. The user attempting to use the token is not the one it was issued for.",
            content = @Content
    )
    @GetMapping("/check")
    @PreAuthorize("hasAuthority('SCOPE_NOT_VERIFIED')")
    @RateLimiter(name = "EmailVerifyController")
    public ResponseEntity<AuthResponseDto> checkToken(
            @Parameter(description = "The verification token received via email.", required = true, example = "FDGDitreKFfdsd")
            @RequestParam("token") String token, HttpServletResponse response,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(emailVerificationService.checkToken(jwt.getSubject(), token, response));
    }
}
