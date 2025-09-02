package com.engly.engly_server.models.dto.request;

import com.engly.engly_server.models.enums.CategoryType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@JsonSubTypes({
        @JsonSubTypes.Type(value = RoomRequest.RoomCreateRequest.class, name = "roomCreate"),
        @JsonSubTypes.Type(value = RoomRequest.RoomUpdateRequest.class, name = "roomUpdate")
})
public sealed interface RoomRequest permits RoomRequest.RoomCreateRequest, RoomRequest.RoomUpdateRequest {
    record RoomUpdateRequest(String name,
                             String description,
                             CategoryType newCategory,
                             String updateCreatorByEmail) implements RoomRequest { }

    record RoomCreateRequest(
            @NotBlank(message = "Name of room is blank")
            @Pattern(
                    regexp = "^[a-zA-Z]{2,50}$",
                    message = "Name must contain only letters (a-z, A-Z) and be between 2 and 50 characters long."
            )
            String name,

            @NotBlank(message = "description of room is blank")
            @Pattern(
                    regexp = "^[a-zA-Z]{2,100}$",
                    message = "Description must contain only letters (a-z, A-Z) and be between 2 and 50 characters long."
            )
            String description) implements RoomRequest { }
}
