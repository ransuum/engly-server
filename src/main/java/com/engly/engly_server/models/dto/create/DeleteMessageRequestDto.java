package com.engly.engly_server.models.dto.create;

import jakarta.validation.constraints.NotBlank;

public record DeleteMessageRequestDto(@NotBlank String messageId,
                                      @NotBlank String roomId) { }
