package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.request.SignUpRequest;
import com.engly.engly_server.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
            summary = "Автентифікація користувача",
            description = """
            Використовуйте Basic Auth у Postman:
            1. Перейдіть до вкладки Authorization та оберіть `Basic Auth`
            2. Введіть email та пароль
            3. Вкажіть URL: `http://localhost:8000/sign-in` або `https://favourable-rodie-java-service-b82e5859.koyeb.app/sign-in`
            4. Оберіть метод `POST` та натисніть `Send`
           \s
            Якщо використовуєте swagger, зверху є кнопка Authorize.
            У відповіді прийде access-токен. Використовуйте його для наступних запитів, передаючи в заголовку `Authorization: Bearer {token}`.
       \s""",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Успішна автентифікація. Повертає access та refresh токени."),
                    @ApiResponse(responseCode = "401", description = "Невірні облікові дані")
            }
    )

    @PostMapping("/sign-in")
    public ResponseEntity<Object> authenticateUser(Authentication authentication,
                                                   HttpServletResponse response) {
        return new ResponseEntity<>(authService.getJwtTokensAfterAuthentication(authentication, response), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Оновлення Access-токену",
            description = """
            Використовуйте Refresh-токен для отримання нового Access-токену.
            У запиті передайте `Authorization: Bearer {refresh_token}`.
        """,
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
        Дозволяє зареєструвати нового користувача.
        У тілі запиту передавайте JSON з необхідними даними.
    """,
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
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest,
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
    public ResponseEntity<Map<String, Boolean>> checkUsernameAvailability(@RequestParam String username) {
        return new ResponseEntity<>(authService.checkUsernameAvailability(username), HttpStatus.OK);
    }

    @Operation(
            summary = "Перевірка при реєстрації на email live",
            description = """
        Дозволяє перевіряти в живую email - це для фронтенду, треба додати listener
    """
    )
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmailAvailability(@RequestParam String email) {
        return new ResponseEntity<>(authService.checkEmailAvailability(email), HttpStatus.OK);
    }
}
