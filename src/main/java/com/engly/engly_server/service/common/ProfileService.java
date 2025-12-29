package com.engly.engly_server.service.common;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.service.mapper.UserMapper;
import com.engly.engly_server.models.dto.request.ProfileUpdateRequest;
import com.engly.engly_server.models.dto.response.UsersDto;
import com.engly.engly_server.repository.UserRepository;
import com.engly.engly_server.utils.CacheName;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.engly.engly_server.exception.handler.ExceptionMessage.PROFILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.USER_PROFILES, key = "#id", sync = true)
    public UsersDto getProfile(String id) {
        return userRepository.findById(id).map(userMapper::toUsersDto)
                .orElseThrow(() -> new NotFoundException(PROFILE_NOT_FOUND));
    }

    @Caching(
            evict = {
                    @CacheEvict(value = CacheName.ALL_USER, allEntries = true),
                    @CacheEvict(value = CacheName.USER_ID, key = "#id"),
                    @CacheEvict(value = CacheName.USER_ENTITY_ID, key = "#id"),
                    @CacheEvict(value = CacheName.USER_PROFILES, key = "#id")
            }
    )
    @Transactional
    public UsersDto updateProfile(String id, ProfileUpdateRequest profileUpdateData) {
        return userRepository.findById(id)
                .map(user -> {
                    if (StringUtils.isNotBlank(profileUpdateData.username()))
                        user.setUsername(profileUpdateData.username());
                    Optional.ofNullable(profileUpdateData.goal())
                            .ifPresent(goals -> user.getAdditionalInfoNonNull().setGoal(goals));
                    Optional.ofNullable(profileUpdateData.englishLevel())
                            .ifPresent(englishLevels -> user.getAdditionalInfoNonNull().setEnglishLevel(englishLevels));
                    return userMapper.toUsersDto(userRepository.save(user));
                })
                .orElseThrow(() -> new NotFoundException(PROFILE_NOT_FOUND));
    }
}
