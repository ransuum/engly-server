package com.engly.engly_server.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity()
@Table(name = "message_reads")
@IdClass(MessageRead.MessageReadId.class)
public class MessageRead {
    @Id
    @ManyToOne
    @JoinColumn(name = "message_id", referencedColumnName = "id")
    private Message message;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;

    @CreationTimestamp
    @Column(nullable = false, name = "read_at")
    private Instant readAt;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public class MessageReadId implements Serializable {
        private Message message;
        private Users user;
    }
}


