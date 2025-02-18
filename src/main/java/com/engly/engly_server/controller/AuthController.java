package com.engly.engly_server.controller;

import com.engly.engly_server.models.request.SignUpRequest;
import com.engly.engly_server.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@Tag(name = "Аутентификация и Авторизация", description = "Контроллер для регистрации, входа и обновления токена")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Аутентификация пользователя",
            description = """
            Используйте Basic Auth в Postman:
            1. Перейдите во вкладку Authorization и выберите `Basic Auth`
            2. Введите email и пароль
            3. Укажите URL: `http://localhost:8000/sign-in`\s
            4. Выберите метод `POST` и нажмите `Send`
           \s
            Если swagger то сверху есть кнопка Authorize.
            В ответе придёт access-токен. Используйте его для последующих запросов, передавая в заголовке `Authorization: Bearer {token}`.
       \s""",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешная аутентификация. Возвращает access и refresh токены."),
                    @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
            }
    )
    @PostMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(Authentication authentication, HttpServletResponse response) {
        return ResponseEntity.ok(authService.getJwtTokensAfterAuthentication(authentication, response));
    }

    @Operation(
            summary = "Обновление Access-токена",
            description = """
            Используйте Refresh-токен для получения нового Access-токена.
            В запросе передайте `Authorization: Bearer {refresh_token}`.
        """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Новый Access-токен успешно получен"),
                    @ApiResponse(responseCode = "403", description = "Refresh-токен недействителен или истёк")
            }
    )
    @PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
    @PostMapping("/refresh-token")
    public ResponseEntity<?> getAccessToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(authService.getAccessTokenUsingRefreshToken(authorizationHeader));
    }

    @Operation(
            summary = "Регистрация нового пользователя",
            description = """
            Позволяет зарегистрировать нового пользователя.
            В теле запроса передавайте JSON с необходимыми данными.
        """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные пользователя для регистрации",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SignUpRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
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
        return ResponseEntity.ok(authService.registerUser(signUpRequest, httpServletResponse));
    }
}
