package com.engly.engly_server.controller;

import com.engly.engly_server.models.request.create.SignUpRequest;
import com.engly.engly_server.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@Tag(name = "Автентифікація та Авторизація", description = "Контролер для реєстрації, входу та оновлення токену")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "User Authentication",
            description = """
                     Use Basic Auth in Postman:
                     1. Go to the Authorization tab and select `Basic Auth`
                     2. Enter username and password
                     3. Specify URL: `http://localhost:8000/sign-in`
                     4. Select `POST` method and click `Send`
                     5. Response: access token, refresh token and details
                     6. Add access token to Bearer
                    \s
                     Use Basic Auth in Swagger:
                     1. Go to the icon lock and use `Basic Auth`
                     2. Enter username and password -> authorize
                     3. Response: access token, refresh token and details
                     4. Try it out -> execute
                     4. Add access token to Bearer auth in swagger
                \s""",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successful authentication. Returns access and refresh tokens."),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials")
            }
    )
    @PostMapping("/sign-in")
    public ResponseEntity<Object> authenticateUser(Authentication authentication, HttpServletResponse response) {
        return new ResponseEntity<>(authService.getJwtTokensAfterAuthentication(authentication, response), HttpStatus.CREATED);
    }

    @Operation(
            summary = "User Authentication",
            description = """
                     Use refresh-token in Postman:
                     1. Go to the Authorization tab and select `Bearer`
                     2. Specify URL: `http://localhost:8000/refresh-token` POST
                     3. Click execute
                     4. Response: access token, refresh token and details
                    \s
                     Use refresh-token in Swagger:
                     1. Go to the icon lock and select `Bearer` -> put refresh token in there:
                     2. Write in param refresh token too
                     3. Click execute
                     4. Response: access token, refresh token and details
                \s""",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Новий Access-токен успішно отримано"),
                    @ApiResponse(responseCode = "403", description = "Refresh-токен недійсний або закінчився термін дії")
            }
    )
    @PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
    @PostMapping("/refresh-token")
    public ResponseEntity<Object> getAccessToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(authService.getAccessTokenUsingRefreshToken(authorizationHeader));
    }

    @Operation(
            summary = "Реєстрація нового користувача",
            description = """
                            For Goals:
                            DEFAULT("Default"),
                            IMPROVE_ENGLISH("Improve English"),
                            LEARN_NEW_LANGUAGE("Learn new language"),
                            MEET_NEW_PEOPLE("Meet new people");
                           \s
                        \s
                        Після введення всіх полів -> отримання access token + refresh token.
                        Так як це Email реєстрація буде видана роль NOT_VERIFIED. Це означає що ви не зможете зробити взагалі запит
                        окрім '/api/notify'. Якщо реєстрація через гугл підтверджувати не треба.
                   \s""",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Дані користувача для реєстрації",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SignUpRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Користувача успішно зареєстровано"),
                    @ApiResponse(responseCode = "400", description = "Помилка валідації вхідних даних")
            }
    )
    @PostMapping("/sign-up")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignUpRequest signUpRequest,
                                                        BindingResult bindingResult, HttpServletResponse httpServletResponse) {

        log.info("[AuthController:registerUser]Signup Process Started for user:{}", signUpRequest.username());
        if (bindingResult.hasErrors()) {
            List<String> errorMessage = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            log.error("[AuthController:registerUser]Errors in user:{}", errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
        return new ResponseEntity<>(authService.registerUser(signUpRequest, httpServletResponse), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Перевірка при реєстрації на username live",
            description = """
                        Дозволяє перевіряти в живую юзернейм - це для фронтенду, треба додати listener
                    """
    )
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsernameAvailability(@NotBlank(message = "Username is blank")
                                                                          @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters.")
                                                                          @Pattern(
                                                                                  regexp = "^[a-zA-Z]{2,50}$",
                                                                                  message = "Username must contain only letters (a-z, A-Z) and be between 2 and 50 characters long."
                                                                          )
                                                                          @RequestParam
                                                                          String username) {
        return new ResponseEntity<>(authService.checkUsernameAvailability(username), HttpStatus.OK);
    }

    @Operation(
            summary = "Перевірка при реєстрації на email live",
            description = """
                        Дозволяє перевіряти в живую email - це для фронтенду, треба додати listener
                    """
    )
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmailAvailability(@RequestParam
                                                                       @Email(message = "Isn't email")
                                                                       @NotBlank(message = "Email is blank")
                                                                       @Size(max = 50, message = "Email cannot exceed 50 characters. Please shorten your input.")
                                                                       String email) {
        return new ResponseEntity<>(authService.checkEmailAvailability(email), HttpStatus.OK);
    }
}
