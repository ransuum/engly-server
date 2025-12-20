package com.engly.engly_server.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import module java.base;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "message_reads", indexes = {
        @Index(name = "idx_user_read_at", columnList = "user_id, read_at"),
        @Index(name = "idx_message_user", columnList = "message_id, user_id")
})
public class MessageRead implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "message_id", nullable = false)
    private String messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;

    @Column(name = "room_id", nullable = false)
    private String roomId;

    @CreationTimestamp
    @Column(nullable = false, name = "read_at")
    private Instant readAt;
}


