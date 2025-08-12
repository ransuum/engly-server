package com.engly.engly_server.exception.handler;

import java.time.LocalDateTime;

public record ExceptionResponse(String message,
                                int code,
                                String details,
                                LocalDateTime timestamp) { }
