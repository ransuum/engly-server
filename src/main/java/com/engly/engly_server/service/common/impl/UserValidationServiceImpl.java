package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.models.dto.AvailabilityResponseDto;
import com.engly.engly_server.models.dto.GoogleAvailabilityDto;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.service.common.UserValidationService;
import com.engly.engly_server.utils.cache.CacheName;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidationServiceImpl implements UserValidationService {
    private final UserRepo userRepo;
    private final SecurityService securityService;

    @Override
    @Cacheable(value = CacheName.USER_FIRST_LOGIN, key =
            "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public GoogleAvailabilityDto firstLogin() {
        return new GoogleAvailabilityDto(!securityService.hasRole("SCOPE_ADDITIONAL_INFO"));
    }

    @Override
    @Cacheable(value = CacheName.USERNAME_AVAILABILITY, key = "#username.toLowerCase()", unless = "#result.available == false")
    public AvailabilityResponseDto isUsernameAvailable(String username) {
        final boolean isAvailable = !userRepo.existsByUsername(username);
        return new AvailabilityResponseDto(isAvailable) ;
    }

    @Override
    @Cacheable(value = CacheName.EMAIL_AVAILABILITY, key = "#email.toLowerCase()", unless = "#result.available == false")
    public AvailabilityResponseDto isEmailAvailable(String email) {
        final boolean isAvailable = !userRepo.existsByEmail(email);
        return new AvailabilityResponseDto(isAvailable);
    }
}
