package com.engly.engly_server.models.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

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

    String roomId();

    record CreateMessageRequest(@NotBlank(message = "Room is empty") String roomId,
                                @Size(max = 200)
                                String content,
                                @Size(max = 150)
                                String imageId) implements MessageRequest {
    }

    record EditMessageRequest(String roomId, String id, String content) implements MessageRequest { }

    record DeleteMessageRequest(String roomId, String messageId) implements MessageRequest { }

    record MarkAsReadRequest(String roomId, List<String> messageIds) implements MessageRequest { }

    record MessageReadersRequest(String roomId, String messageId, int page, int size)
            implements MessageRequest { }

    record TypingRequest(String roomId, boolean isTyping) implements MessageRequest { }

    default String content() {
        return switch (this) {
            case CreateMessageRequest createMessageRequest -> createMessageRequest.content;
            case EditMessageRequest editMessageRequest -> editMessageRequest.content;
            default -> throw new ResponseStatusException(
                    HttpStatusCode.valueOf(500), "Unexpected value: " + this);
        };
    }

    default String imageId() {
        return switch (this) {
            case CreateMessageRequest createMessageRequest -> createMessageRequest.imageId;
            default -> throw new ResponseStatusException(
                    HttpStatusCode.valueOf(500), "Unexpected value: " + this);
        };
    }

    default String messageId() {
        return switch (this) {
            case DeleteMessageRequest deleteMessageRequest -> deleteMessageRequest.messageId;
            case MessageReadersRequest messageReadersRequest -> messageReadersRequest.messageId;
            default -> throw new ResponseStatusException(
                    HttpStatusCode.valueOf(500), "Unexpected value: " + this);
        };
    }

    default List<String> messageIds() {
        return switch (this) {
            case MarkAsReadRequest markAsReadRequest -> markAsReadRequest.messageIds;
            default -> throw new ResponseStatusException(
                    HttpStatusCode.valueOf(500), "Unexpected value: " + this);
        };
    }
}
