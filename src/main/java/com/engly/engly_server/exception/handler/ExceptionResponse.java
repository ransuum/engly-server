package com.engly.engly_server.exception.handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record ExceptionResponse(String message,
                                int code,
                                String details,
                                String timestamp) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public static ExceptionResponse of(String message, int code, String details) {
        return new ExceptionResponse(
                message,
                code,
                details,
                LocalDateTime.now().format(FORMATTER)
        );
    }
}
