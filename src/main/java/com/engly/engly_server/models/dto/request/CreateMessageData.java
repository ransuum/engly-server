package com.engly.engly_server.models.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMessageData(@NotBlank(message = "Room is empty") String roomId,
                                @Size(max = 200)
                                String content,
                                @Size(max = 150)
                                String imageId) {
}
