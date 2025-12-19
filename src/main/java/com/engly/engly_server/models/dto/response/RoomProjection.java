package com.engly.engly_server.models.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "room_projection")
public class RoomProjection {
    @Id
    @Column(name = "id")
    private String id;
    private String name;
    @Nullable
    private String description;
    @Nullable
    @Column(name = "last_message")
    private String lastMessage;
    @Nullable
    @Column(name = "last_message_created_at")
    private Instant lastMessageCreatedAt;
    private Long members;
}
