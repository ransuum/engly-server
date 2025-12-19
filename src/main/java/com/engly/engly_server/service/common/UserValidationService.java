package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.response.AvailabilityResponseDto;
import com.engly.engly_server.models.dto.response.GoogleAvailabilityDto;
import com.engly.engly_server.repository.UserRepository;
import com.engly.engly_server.security.config.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidationService {

    private final UserRepository userRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public GoogleAvailabilityDto firstLogin() {
        return new GoogleAvailabilityDto(!authenticatedUserProvider.hasRole("SCOPE_ADDITIONAL_INFO"));
    }

    public AvailabilityResponseDto isUsernameAvailable(String username) {
        var isAvailable = !userRepository.existsByUsername(username);
        return new AvailabilityResponseDto(isAvailable) ;
    }

    public AvailabilityResponseDto isEmailAvailable(String email) {
        var isAvailable = !userRepository.existsByEmail(email);
        return new AvailabilityResponseDto(isAvailable);
    }
}
