package com.engly.engly_server.models.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "messages", indexes = {
        @Index(name = "idx_messages_room_created", columnList = "room_id, created_at DESC"),
        @Index(name = "idx_messages_room_content", columnList = "room_id, content"),
        @Index(name = "idx_messages_user", columnList = "user_id"),
        @Index(name = "idx_messages_created", columnList = "created_at DESC"),
        @Index(name = "idx_messages_room_user", columnList = "room_id, user_id"),
        @Index(name = "idx_messages_content", columnList = "content")
})
@ToString(exclude = {"room", "user", "messageReads"})
public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private Rooms room;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private Users user;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MessageRead> messageReads = new ArrayList<>();

    @Column(nullable = false)
    private String content;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    private Boolean isEdited;
    private Boolean isDeleted;
}
