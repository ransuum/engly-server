package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.config.AbstractTestcontainersConfiguration;
import com.engly.engly_server.config.TestJpaConfiguration;
import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.response.UserSettingsDto;
import com.engly.engly_server.models.entity.UserSettings;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.NativeLanguage;
import com.engly.engly_server.models.enums.Provider;
import com.engly.engly_server.models.enums.Theme;
import com.engly.engly_server.repository.UserRepository;
import com.engly.engly_server.repository.UserSettingsRepository;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.service.common.UserService;
import com.engly.engly_server.service.common.UserSettingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {TestJpaConfiguration.class, UserSettingServiceImpl.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class UserSettingServiceImplTest extends AbstractTestcontainersConfiguration {

    @Autowired
    private UserSettingService userSettingService;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private SecurityService securityService;

    @MockitoBean
    private UserService userService;

    private Users testUser;
    private UserSettings testUserSettings;
    private String testUserId;

    @BeforeEach
    void setUp() {
        userSettingsRepository.deleteAll();
        userRepository.deleteAll();

        String testEmail = "test@example.com";
        testUser = Users.builder()
                // Remove .id(testUserId) - let JPA generate the ID
                .username("testuser")
                .email(testEmail)
                .password("password123")
                .roles("ROLE_USER")
                .provider(Provider.GOOGLE)
                .emailVerified(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        // Don't setup mocks here - do it in setupUserAndSettings()
    }

    private void setupUserAndSettings() {
        // Save user first and get the generated ID
        testUser = userRepository.save(testUser);
        testUserId = testUser.getId(); // Get the actual generated ID

        // Setup mocks with the actual user ID
        when(securityService.getCurrentUserEmail()).thenReturn(testUser.getEmail());
        when(userService.getUserIdByEmail(testUser.getEmail())).thenReturn(testUserId);

        // Create settings with the managed user entity and generated ID
        testUserSettings = UserSettings.builder()
                .id(testUserId) // Use the actual generated ID
                .user(testUser)
                .theme(Theme.DARK)
                .notifications(true)
                .interfaceLanguage(NativeLanguage.ENGLISH)
                .build();

        testUserSettings = userSettingsRepository.save(testUserSettings);
    }

    @Test
    @DisplayName("Should return user settings when user settings exist")
    void getById_UserSettingsExist_ReturnsUserSettingsDto() {
        // Arrange
        setupUserAndSettings();

        // Act
        UserSettingsDto result = userSettingService.getById(testUserId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(testUserId);
        assertThat(result.theme()).isEqualTo(Theme.DARK);
        assertThat(result.notifications()).isTrue();
        assertThat(result.interfaceLanguage()).isEqualTo(NativeLanguage.ENGLISH);
    }

    @Test
    @DisplayName("Should throw NotFoundException when user settings do not exist")
    void getById_UserSettingsDoNotExist_ThrowsNotFoundException() {
        // Arrange
        String nonExistentId = "non-existent-id";

        // Act & Assert
        assertThatThrownBy(() -> userSettingService.getById(nonExistentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("UserSettings not found");
    }

    @Test
    @DisplayName("Should update theme when valid theme provided")
    void update_ValidTheme_UpdatesTheme() {
        // Arrange
        setupUserAndSettings();

        // Act
        userSettingService.update(testUserId, null, Theme.BRIGHT);

        // Assert
        UserSettings updatedSettings = userSettingsRepository.findById(testUserId).orElseThrow();
        assertThat(updatedSettings.getTheme()).isEqualTo(Theme.BRIGHT);
        assertThat(updatedSettings.isNotifications()).isTrue(); // Should remain unchanged
        assertThat(updatedSettings.getInterfaceLanguage()).isEqualTo(NativeLanguage.ENGLISH); // Should remain unchanged
    }

    @Test
    @DisplayName("Should update notifications when valid notifications value provided")
    void update_ValidNotifications_UpdatesNotifications() {
        // Arrange
        setupUserAndSettings();

        // Act
        userSettingService.update(testUserId, false, null);

        // Assert
        UserSettings updatedSettings = userSettingsRepository.findById(testUserId).orElseThrow();
        assertThat(updatedSettings.isNotifications()).isFalse();
        assertThat(updatedSettings.getTheme()).isEqualTo(Theme.DARK); // Should remain unchanged
        assertThat(updatedSettings.getInterfaceLanguage()).isEqualTo(NativeLanguage.ENGLISH); // Should remain unchanged
    }

    @Test
    @DisplayName("Should update both theme and notifications when both valid values provided")
    void update_ValidThemeAndNotifications_UpdatesBothFields() {
        // Arrange
        setupUserAndSettings();

        // Act
        userSettingService.update(testUserId, false, Theme.CYAN);

        // Assert
        UserSettings updatedSettings = userSettingsRepository.findById(testUserId).orElseThrow();
        assertThat(updatedSettings.getTheme()).isEqualTo(Theme.CYAN);
        assertThat(updatedSettings.isNotifications()).isFalse();
        assertThat(updatedSettings.getInterfaceLanguage()).isEqualTo(NativeLanguage.ENGLISH); // Should remain unchanged
    }

    @Test
    @DisplayName("Should not update when null values provided")
    void update_NullValues_DoesNotUpdate() {
        // Arrange
        setupUserAndSettings();

        // Act
        userSettingService.update(testUserId, null, null);

        // Assert
        UserSettings updatedSettings = userSettingsRepository.findById(testUserId).orElseThrow();
        assertThat(updatedSettings.getTheme()).isEqualTo(Theme.DARK); // Should remain unchanged
        assertThat(updatedSettings.isNotifications()).isTrue(); // Should remain unchanged
        assertThat(updatedSettings.getInterfaceLanguage()).isEqualTo(NativeLanguage.ENGLISH); // Should remain unchanged
    }

    @Test
    @DisplayName("Should throw NotFoundException when trying to update non-existent user settings")
    void update_UserSettingsDoNotExist_ThrowsNotFoundException() {
        // Arrange
        String nonExistentId = "non-existent-id";

        // Act & Assert
        assertThatThrownBy(() -> userSettingService.update(nonExistentId, false, Theme.BRIGHT))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Should update theme to all available theme values")
    void update_AllThemeValues_UpdatesSuccessfully() {
        // Arrange
        setupUserAndSettings();

        // Test DARK theme
        userSettingService.update(testUserId, null, Theme.DARK);
        UserSettings settings = userSettingsRepository.findById(testUserId).orElseThrow();
        assertThat(settings.getTheme()).isEqualTo(Theme.DARK);

        // Test BRIGHT theme
        userSettingService.update(testUserId, null, Theme.BRIGHT);
        settings = userSettingsRepository.findById(testUserId).orElseThrow();
        assertThat(settings.getTheme()).isEqualTo(Theme.BRIGHT);

        // Test CYAN theme
        userSettingService.update(testUserId, null, Theme.CYAN);
        settings = userSettingsRepository.findById(testUserId).orElseThrow();
        assertThat(settings.getTheme()).isEqualTo(Theme.CYAN);
    }

    @Test
    @DisplayName("Should toggle notifications between true and false")
    void update_ToggleNotifications_UpdatesSuccessfully() {
        // Arrange
        setupUserAndSettings();

        // Initially true, set to false
        userSettingService.update(testUserId, false, null);
        UserSettings settings = userSettingsRepository.findById(testUserId).orElseThrow();
        assertThat(settings.isNotifications()).isFalse();

        // Set back to true
        userSettingService.update(testUserId, true, null);
        settings = userSettingsRepository.findById(testUserId).orElseThrow();
        assertThat(settings.isNotifications()).isTrue();
    }

    @Test
    @DisplayName("Should handle different user settings with different initial values")
    void update_DifferentInitialValues_UpdatesCorrectly() {
        // Arrange
        setupUserAndSettings();
        // Modify the saved settings
        testUserSettings.setTheme(Theme.BRIGHT);
        testUserSettings.setNotifications(false);
        userSettingsRepository.save(testUserSettings);

        // Act
        userSettingService.update(testUserId, true, Theme.DARK);

        // Assert
        UserSettings updatedSettings = userSettingsRepository.findById(testUserId).orElseThrow();
        assertThat(updatedSettings.getTheme()).isEqualTo(Theme.DARK);
        assertThat(updatedSettings.isNotifications()).isTrue();
        assertThat(updatedSettings.getInterfaceLanguage()).isEqualTo(NativeLanguage.ENGLISH);
    }
}