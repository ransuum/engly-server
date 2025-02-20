package com.engly.engly_server.models.entity;

import com.engly.engly_server.models.enums.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "users")
public class Users implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(name = "USER_NAME", unique = true, nullable = false)
    private String username;

    @Column(nullable = false, name = "EMAIL_ID", unique = true)
    private String email;

    @Column(nullable = false, name = "PASSWORD")
    private String password;

    @Column(nullable = false, name = "ROLES")
    private String roles;

    @Column(nullable = false)
    private Boolean emailVerified;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    @CreationTimestamp
    @Column(nullable = false, name = "updated_at")
    private Instant updatedAt;

    private Instant lastLogin;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    private String providerId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "additional_info_id", referencedColumnName = "id")
    private AdditionalInfo additionalInfo;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RefreshToken> refreshTokens;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Rooms> rooms;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ActivityLogs> activityLogs;

    @OneToMany(mappedBy = "moder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Moderation> moderations;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserSettings> userSettings;

}
