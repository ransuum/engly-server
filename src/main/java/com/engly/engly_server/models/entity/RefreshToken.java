package com.engly.engly_server.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_tokens")
@ToString(exclude = {"user"})
public class RefreshToken implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, length = 10000)
    private String token;

    @Column(name = "revoked")
    private boolean revoked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;

    @Column(name = "CREATED_AT")
    private Instant createdAt;

    @Column(name = "EXPIRES_AT")
    private Instant expiresAt;
}
