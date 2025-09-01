package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.config.AbstractTestcontainersConfiguration;
import com.engly.engly_server.config.DataJpaTestWithContainer;
import com.engly.engly_server.models.dto.response.AvailabilityResponseDto;
import com.engly.engly_server.models.dto.response.GoogleAvailabilityDto;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Provider;
import com.engly.engly_server.repository.UserRepository;
import com.engly.engly_server.security.config.AuthenticatedUserProvider;
import com.engly.engly_server.service.common.UserValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DataJpaTestWithContainer
@Import(UserValidationServiceImpl.class)
class UserValidationServiceImplTest extends AbstractTestcontainersConfiguration {

    @Autowired
    private UserValidationService userValidationService;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private AuthenticatedUserProvider authenticatedUserProvider;

    private Users testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = Users.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .roles("ROLE_USER")
                .provider(Provider.GOOGLE)
                .emailVerified(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Should return user exists false when user has SCOPE_ADDITIONAL_INFO role")
    void firstLogin_UserHasAdditionalInfoScope_ReturnsFalse() {
        // Arrange
        when(authenticatedUserProvider.hasRole("SCOPE_ADDITIONAL_INFO")).thenReturn(true);

        // Act
        GoogleAvailabilityDto result = userValidationService.firstLogin();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.userExists()).isFalse();
    }

    @Test
    @DisplayName("Should return user exists true when user does not have SCOPE_ADDITIONAL_INFO role")
    void firstLogin_UserDoesNotHaveAdditionalInfoScope_ReturnsTrue() {
        // Arrange
        when(authenticatedUserProvider.hasRole("SCOPE_ADDITIONAL_INFO")).thenReturn(false);

        // Act
        GoogleAvailabilityDto result = userValidationService.firstLogin();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.userExists()).isTrue();
    }

    @Test
    @DisplayName("Should return available true when username does not exist")
    void isUsernameAvailable_UsernameDoesNotExist_ReturnsTrue() {
        // Act
        AvailabilityResponseDto result = userValidationService.isUsernameAvailable("nonexistentuser");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.available()).isTrue();
    }

    @Test
    @DisplayName("Should return available false when username exists")
    void isUsernameAvailable_UsernameExists_ReturnsFalse() {
        // Arrange
        userRepository.save(testUser);

        // Act
        AvailabilityResponseDto result = userValidationService.isUsernameAvailable("testuser");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.available()).isFalse();
    }

    @Test
    @DisplayName("Should return available true for case-sensitive username check")
    void isUsernameAvailable_CaseSensitiveUsername_ReturnsTrue() {
        // Arrange
        userRepository.save(testUser);

        // Act
        AvailabilityResponseDto result = userValidationService.isUsernameAvailable("TestUser");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.available()).isTrue();
    }

    @Test
    @DisplayName("Should return available true when email does not exist")
    void isEmailAvailable_EmailDoesNotExist_ReturnsTrue() {
        // Act
        AvailabilityResponseDto result = userValidationService.isEmailAvailable("nonexistent@example.com");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.available()).isTrue();
    }

    @Test
    @DisplayName("Should return available false when email exists")
    void isEmailAvailable_EmailExists_ReturnsFalse() {
        // Arrange
        userRepository.save(testUser);

        // Act
        AvailabilityResponseDto result = userValidationService.isEmailAvailable("test@example.com");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.available()).isFalse();
    }

    @Test
    @DisplayName("Should return available true for case-sensitive email check")
    void isEmailAvailable_CaseSensitiveEmail_ReturnsTrue() {
        // Arrange
        userRepository.save(testUser);

        // Act
        AvailabilityResponseDto result = userValidationService.isEmailAvailable("TEST@EXAMPLE.COM");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.available()).isTrue();
    }

    @Test
    @DisplayName("Should handle null username gracefully")
    void isUsernameAvailable_NullUsername_ReturnsTrue() {
        // Act
        AvailabilityResponseDto result = userValidationService.isUsernameAvailable(null);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.available()).isTrue();
    }

    @Test
    @DisplayName("Should handle null email gracefully")
    void isEmailAvailable_NullEmail_ReturnsTrue() {
        // Act
        AvailabilityResponseDto result = userValidationService.isEmailAvailable(null);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.available()).isTrue();
    }

    @Test
    @DisplayName("Should handle empty username")
    void isUsernameAvailable_EmptyUsername_ReturnsTrue() {
        // Act
        AvailabilityResponseDto result = userValidationService.isUsernameAvailable("");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.available()).isTrue();
    }

    @Test
    @DisplayName("Should handle empty email")
    void isEmailAvailable_EmptyEmail_ReturnsTrue() {
        // Act
        AvailabilityResponseDto result = userValidationService.isEmailAvailable("");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.available()).isTrue();
    }
}