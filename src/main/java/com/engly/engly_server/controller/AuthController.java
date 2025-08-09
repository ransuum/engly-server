package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.response.AuthResponseDto;
import com.engly.engly_server.models.dto.request.SignInRequest;
import com.engly.engly_server.models.dto.request.SignUpRequest;
import com.engly.engly_server.service.common.AuthService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Tag(name = "01. Authentication", description = "Endpoints for user sign-up, sign-in, and token management.")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Sign in a user",
            description = "Authenticates a user with their credentials (e.g., email and password) and returns JWT tokens. " +
                    "This endpoint uses Basic Authentication."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful. Returns new JWT tokens and user details.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. Invalid credentials provided.",
                    content = @Content
            )
    })
    @PostMapping("/sign-in")
    @RateLimiter(name = "AuthController")
    public ResponseEntity<AuthResponseDto> authenticateUser(@Valid @RequestBody SignInRequest signInDto, HttpServletResponse response) {
        return ResponseEntity.ok(authService.getJwtTokensAfterAuthentication(signInDto, response));
    }

    @Operation(
            summary = "Register a new user",
            description = """
                          Creates a new user account with the provided details.
                          Upon successful registration via email, the user is assigned a `NOT_VERIFIED` role and must verify their email before accessing most protected endpoints.
                          
                          Available `goals` enum values:
                          - `DEFAULT`
                          - `IMPROVE_ENGLISH`
                          - `LEARN_NEW_LANGUAGE`
                          - `MEET_NEW_PEOPLE`
                          """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully registered. Returns JWT tokens and user details.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request. Validation failed (e.g., username/email already exists, password too weak, invalid goal).",
                    content = @Content
            )
    })
    @PostMapping("/sign-up")
    @RateLimiter(name = "AuthController")
    public ResponseEntity<Object> signUpUser(@Valid @RequestBody SignUpRequest signUpRequestDto,
                                             BindingResult bindingResult, HttpServletResponse httpServletResponse) {

        log.info("[AuthController:registerUser]Signup Process Started for user:{}", signUpRequestDto.username());
        if (bindingResult.hasErrors()) {
            List<String> errorMessage = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            log.error("[AuthController:registerUser]Errors in user:{}", errorMessage);
            return ResponseEntity.status(400).body(errorMessage);
        }
        return ResponseEntity.status(201).body(authService.registerUser(signUpRequestDto, httpServletResponse));
    }

    @Operation(
            summary = "Refresh an access token",
            description = "Generates a new access token using a valid refresh token. The refresh token must be provided in an `httpOnly` cookie."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Access token successfully refreshed.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. The provided refresh token is invalid, expired, or missing.",
                    content = @Content
            )
    })
    @PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
    @PostMapping("/refresh-token")
    @RateLimiter(name = "AuthController")
    public ResponseEntity<AuthResponseDto> getAccessToken(
            @CookieValue(value = "refreshToken", required = false) String refreshTokenFromCookie,
            HttpServletResponse httpServletResponse) {
        return ResponseEntity.ok(authService.getAccessTokenUsingRefreshToken(refreshTokenFromCookie, httpServletResponse));
    }
}
