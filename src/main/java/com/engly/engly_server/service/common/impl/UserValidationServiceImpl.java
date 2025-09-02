package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.models.dto.response.AvailabilityResponseDto;
import com.engly.engly_server.models.dto.response.GoogleAvailabilityDto;
import com.engly.engly_server.repository.UserRepository;
import com.engly.engly_server.security.config.AuthenticatedUserProvider;
import com.engly.engly_server.service.common.UserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidationServiceImpl implements UserValidationService {

    private final UserRepository userRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    public GoogleAvailabilityDto firstLogin() {
        return new GoogleAvailabilityDto(!authenticatedUserProvider.hasRole("SCOPE_ADDITIONAL_INFO"));
    }

    @Override
    public AvailabilityResponseDto isUsernameAvailable(String username) {
        final boolean isAvailable = !userRepository.existsByUsername(username);
        return new AvailabilityResponseDto(isAvailable) ;
    }

    @Override
    public AvailabilityResponseDto isEmailAvailable(String email) {
        final boolean isAvailable = !userRepository.existsByEmail(email);
        return new AvailabilityResponseDto(isAvailable);
    }
}
