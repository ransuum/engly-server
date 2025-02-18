package com.engly.engly_server.models.entity;

import com.engly.engly_server.models.enums.EnglishLevels;
import com.engly.engly_server.models.enums.Gender;
import com.engly.engly_server.models.enums.Goals;
import com.engly.engly_server.models.enums.NativeLanguage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "additional_info_user")
public class AdditionalInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private EnglishLevels englishLevel;

    @Enumerated(EnumType.STRING)
    private NativeLanguage nativeLanguage;

    @Enumerated(EnumType.STRING)
    private Goals goals;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne(mappedBy = "additionalInfo")
    private Users users;
}
