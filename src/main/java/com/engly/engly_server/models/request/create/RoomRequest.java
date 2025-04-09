package com.engly.engly_server.models.request.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoomRequest(@NotBlank(message = "Name of room is blank")
                          @Size(min = 2, max = 50)
                          String name,
                          @NotBlank(message = "Description of room is blank")
                          String description) {
}
