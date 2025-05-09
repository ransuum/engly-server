package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.dto.EmailSendInfo;
import com.engly.engly_server.models.dto.update.PasswordResetRequest;
import com.engly.engly_server.service.notification.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password-reset")
@Slf4j
@Tag(name = "Підтвердження email", description = "Контроллер для підтвердження email")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }


    @Operation(
            summary = "Надсилання посилання на email для відновлення паролю",
            description = """
                         Вам на пошту прийде лист з посиланням для відновлення паролю перейдіть по ньому і виконається запит `http://localhost:8000/api/password-reset`,
                         але повинен бути аксес токен у Bearer(отриманий при логіні для юзера без паролю)
                    \s""",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Посилання було надіслано на email"),
                    @ApiResponse(responseCode = "409", description = "Посилання не було надіслано")
            }
    )
    @PreAuthorize("hasAuthority('SCOPE_PASSWORD_RESET')")
    @PostMapping("/send")
    public ResponseEntity<EmailSendInfo> notifyUserPasswordReset(@RequestParam String email) {
        try {
            return new ResponseEntity<>(passwordResetService.sendMessage(email), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Відновлення паролю за допомогою посилання",
            description = """
                         1. Перейдіть по згенерованому посиланню яке надійшло на email
                         На почту приходить повідомлення у вигляді: http://localhost:8000/api/password-reset/password-reset?token.
                         Копіюйте тільки password та вставляеєте його у параметр. Після чого отримуєте знову але оновлений access token та рефреш токен.
                         Як ввели аксес токен у Bearer, тепер можете робити запити.
                        \s
                    \s""",
            responses = {
                    @ApiResponse(responseCode = "205", description = "Успішне зміна паролю"),
                    @ApiResponse(responseCode = "404", description = "Пароль не був відновлений")
            }
    )
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_PASSWORD_RESET')")
    public ResponseEntity<AuthResponseDto> passwordReset(@RequestBody PasswordResetRequest data) {
        return new ResponseEntity<>(passwordResetService.passwordReset(data), HttpStatus.RESET_CONTENT);
    }
}
