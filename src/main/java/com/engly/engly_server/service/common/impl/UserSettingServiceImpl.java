package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.service.mapper.UserSettingsMapper;
import com.engly.engly_server.models.dto.response.UserSettingsDto;
import com.engly.engly_server.models.enums.Theme;
import com.engly.engly_server.repository.UserSettingsRepository;
import com.engly.engly_server.service.common.UserSettingService;
import com.engly.engly_server.utils.CacheName;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.engly.engly_server.exception.handler.ExceptionMessage.USER_NOT_FOUND;
import static com.engly.engly_server.exception.handler.ExceptionMessage.USER_SETTINGS_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserSettingServiceImpl implements UserSettingService {

    private final UserSettingsRepository userSettingsRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.USER_SETTINGS, key = "#id", sync = true)
    public UserSettingsDto getById(String id) {
        return userSettingsRepository.findById(id)
                .map(UserSettingsMapper.INSTANCE::toUserSettingsDto)
                .orElseThrow(() -> new NotFoundException(USER_SETTINGS_NOT_FOUND));
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheName.USER_SETTINGS, key = "#id")
    public void update(String id, @Nullable Boolean notifications, @Nullable Theme theme) {
        userSettingsRepository.findById(id)
                .ifPresentOrElse(userSettings -> {
                    if (theme != null) userSettings.setTheme(theme);
                    if (notifications != null) userSettings.setNotifications(notifications);
                    userSettingsRepository.save(userSettings);
                }, () -> { throw new NotFoundException(USER_NOT_FOUND); });
    }
}
