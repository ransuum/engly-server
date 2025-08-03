package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.dto.update.ProfileUpdateRequest;
import com.engly.engly_server.repository.UserRepository;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.service.common.ProfileService;
import com.engly.engly_server.mapper.UserMapper;
import com.engly.engly_server.utils.cache.CacheName;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.engly.engly_server.utils.fieldvalidation.FieldUtil.isValid;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserRepository userRepository;
    private final SecurityService securityService;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.USER_PROFILES, key = "@securityService.getCurrentUserEmail()")
    public UsersDto getProfile() {
        final var email = securityService.getCurrentUserEmail();
        final var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PROFILE));
        return UserMapper.INSTANCE.toUsersDto(user);
    }

    @Override
    @Caching(
            put = {
                    @CachePut(value = CacheName.USER_PROFILES, key = "#result.username"),
                    @CachePut(value = CacheName.USER_ID, key = "#result.id"),
                    @CachePut(value = CacheName.USER_BY_EMAIL_DTO, key = "@securityService.getCurrentUserEmail()")
            },
            evict = {
                    @CacheEvict(value = CacheName.ALL_USER, allEntries = true),
                    @CacheEvict(value = CacheName.USERNAME_AVAILABILITY,
                            key = "#profileUpdateData.username().toLowerCase()", condition = "#profileUpdateData.username() != null")
            }
    )
    @Transactional
    public UsersDto updateProfile(ProfileUpdateRequest profileUpdateData) {
        final var email = securityService.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .map(user -> {
                    if (isValid(profileUpdateData.username())) user.setUsername(profileUpdateData.username());
                    if (isValid(profileUpdateData.goal())) user.getAdditionalInfo().setGoal(profileUpdateData.goal());
                    if (isValid(profileUpdateData.englishLevel()))
                        user.getAdditionalInfo().setEnglishLevel(profileUpdateData.englishLevel());
                    if (isValid(profileUpdateData.nativeLanguage()))
                        user.getAdditionalInfo().setNativeLanguage(profileUpdateData.nativeLanguage());

                    final var savedUser = userRepository.save(user);
                    return UserMapper.INSTANCE.toUsersDto(savedUser);
                })
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PROFILE));
    }
}
