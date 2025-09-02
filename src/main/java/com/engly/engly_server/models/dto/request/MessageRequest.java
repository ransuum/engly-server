package com.engly.engly_server.models.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Pageable;

import java.util.List;

@JsonSubTypes({
        @JsonSubTypes.Type(value = MessageRequest.CreateMessageRequest.class, name = "createMessage"),
        @JsonSubTypes.Type(value = MessageRequest.EditMessageRequest.class, name = "editMessage"),
        @JsonSubTypes.Type(value = MessageRequest.DeleteMessageRequest.class, name = "deleteMessage"),
        @JsonSubTypes.Type(value = MessageRequest.MarkAsReadRequest.class, name = "markRead"),
        @JsonSubTypes.Type(value = MessageRequest.MessageReadersRequest.class, name = "readers"),
        @JsonSubTypes.Type(value = MessageRequest.TypingRequest.class, name = "typing")
})
public sealed interface MessageRequest permits
        MessageRequest.CreateMessageRequest, MessageRequest.EditMessageRequest, MessageRequest.DeleteMessageRequest,
        MessageRequest.MarkAsReadRequest, MessageRequest.MessageReadersRequest, MessageRequest.TypingRequest {
    record CreateMessageRequest(@NotBlank(message = "Room is empty") String roomId,
                                @Size(max = 200)
                                String content,
                                @Size(max = 150)
                                String imageId) implements MessageRequest { }

    record EditMessageRequest(String roomId, String id, String content) implements MessageRequest { }

    record DeleteMessageRequest(String roomId, String messageId) implements MessageRequest { }

    record MarkAsReadRequest(String roomId, List<String> messageId) implements MessageRequest { }

    record MessageReadersRequest(String roomId, String messageId, Pageable pageable) implements MessageRequest { }

    record TypingRequest(String roomId, boolean isTyping) implements MessageRequest { }
}
