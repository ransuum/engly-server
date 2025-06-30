package com.engly.engly_server.models.entity;

import com.engly.engly_server.models.enums.NativeLanguage;
import com.engly.engly_server.models.enums.Theme;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "user_settings")
public class UserSettings implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;

    @Enumerated(EnumType.STRING)
    private Theme theme;

    private boolean notifications;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "interface_language")
    private NativeLanguage interfaceLanguage;
}
