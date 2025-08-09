package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.response.AvailabilityResponseDto;
import com.engly.engly_server.models.dto.response.GoogleAvailabilityDto;

public interface UserValidationService {
    GoogleAvailabilityDto firstLogin();

    AvailabilityResponseDto isUsernameAvailable(String username);

    AvailabilityResponseDto isEmailAvailable(String email);
}
