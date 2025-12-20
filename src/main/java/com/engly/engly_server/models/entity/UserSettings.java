package com.engly.engly_server.models.entity;

import com.engly.engly_server.models.enums.NativeLanguage;
import com.engly.engly_server.models.enums.Theme;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import module java.base;

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
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Users user;

    @Enumerated(EnumType.STRING)
    private Theme theme;

    private boolean notifications;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "interface_language")
    private NativeLanguage interfaceLanguage;
}
