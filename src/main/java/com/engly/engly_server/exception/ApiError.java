package com.engly.engly_server.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiError {
    private int status;
    private String message;
    private String details;
    private LocalDateTime timestamp;

    public ApiError(int status, String message, String details) {
        this.status = status;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}
