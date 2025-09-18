package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.mapper.UserMapper;
import com.engly.engly_server.models.dto.request.ProfileUpdateRequest;
import com.engly.engly_server.models.dto.response.UsersDto;
import com.engly.engly_server.repository.UserRepository;
import com.engly.engly_server.service.common.ProfileService;
import com.engly.engly_server.utils.cache.CacheName;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.USER_PROFILES, key = "#id")
    public UsersDto getProfile(String id) {
        return userRepository.findById(id)
                .map(UserMapper.INSTANCE::toUsersDto)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PROFILE));
    }

    @Override
    @Caching(
            put = {
                    @CachePut(value = CacheName.USER_PROFILES, key = "#id"),
                    @CachePut(value = CacheName.USER_ID, key = "#result.id")
            },
            evict = {
                    @CacheEvict(value = CacheName.ALL_USER, allEntries = true)
            }
    )
    @Transactional
    public UsersDto updateProfile(String id, ProfileUpdateRequest profileUpdateData) {
        return userRepository.findById(id)
                .map(user -> {
                    if (StringUtils.isNotBlank(profileUpdateData.username()))
                        user.setUsername(profileUpdateData.username());
                    if (profileUpdateData.goal() != null)
                        user.getAdditionalInfo().setGoal(profileUpdateData.goal());
                    if (profileUpdateData.englishLevel() != null)
                        user.getAdditionalInfo().setEnglishLevel(profileUpdateData.englishLevel());
                    if (profileUpdateData.nativeLanguage() != null)
                        user.getAdditionalInfo().setNativeLanguage(profileUpdateData.nativeLanguage());

                    return UserMapper.INSTANCE.toUsersDto(userRepository.save(user));
                })
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PROFILE));
    }
}
