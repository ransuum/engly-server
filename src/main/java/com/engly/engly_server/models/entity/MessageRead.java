package com.engly.engly_server.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "message_reads")
@IdClass(MessageRead.MessageReadId.class)
public class MessageRead implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "message_id")
    private String messageId;

    @Id
    @Column(name = "user_id")
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", insertable = false, updatable = false)
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private Users user;

    @CreationTimestamp
    @Builder.Default
    @Column(nullable = false, name = "read_at")
    private Instant readAt = Instant.now();

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class MessageReadId implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String messageId;
        private String userId;
    }
}


