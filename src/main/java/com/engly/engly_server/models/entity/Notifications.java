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
@Table(name = "notifications")
public class Notifications implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "user_id")
    private Users user;

    @Column(nullable = false)
    private String content;

    private Boolean isRead;

    @CreationTimestamp
    @Column(nullable = false)
    private Instant createdAt;
}
