package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.dto.EmailSendInfo;
import com.engly.engly_server.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notify")
@Slf4j
@Tag(name = "Підтвердження email", description = "Контроллер для підтвердження email")
public class NotifyController {

    private final NotificationService notificationService;

    public NotifyController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(
            summary = "Надсилання посилання на email",
            description = """
                         1. Введіть email
                         2. Вкажіть URL: `http://localhost:8000/api/notify`\s
                         4. Выберите метод `POST` и нажмите `Send`
                        \s
                         Вам на пошту прийде лист з посиланням для підтвердження email перейдіть по ньому і виконається запит `http://localhost:8000/api/notify/check`
                    \s""",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Посилання було надіслано на email"),
                    @ApiResponse(responseCode = "409", description = "Посилання не було надіслано або token не був згенерований коректно")
            }
    )
    @PreAuthorize("hasAuthority('SCOPE_NOT_VERIFIED')")
    @PostMapping
    public ResponseEntity<EmailSendInfo> notifyUser() {
        try {
            return new ResponseEntity<>(notificationService.sendNotifyMessage(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Підтвердження email за допомогою посилання",
            description = """
                         1. Перейдіть по згенерованому посиланню яке надійшло на email
                         Або
                         1. Виконайте запит `http://localhost:8000/notify/check?token={token}&email={email}`
                        \s
                    \s""",
            responses = {
                    @ApiResponse(responseCode = "202", description = "Успішне підтвердження email"),
                    @ApiResponse(responseCode = "404", description = "Email не був підтверджений")
            }
    )
    @GetMapping("/check")
    @PreAuthorize("hasAuthority('SCOPE_NOT_VERIFIED')")
    public ResponseEntity<AuthResponseDto> checkToken(@RequestParam("token") String token) {
        return new ResponseEntity<>(notificationService.checkToken(token), HttpStatus.OK);
    }
}
