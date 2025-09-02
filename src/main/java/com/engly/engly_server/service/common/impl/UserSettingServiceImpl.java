package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.mapper.UserSettingsMapper;
import com.engly.engly_server.models.dto.response.UserSettingsDto;
import com.engly.engly_server.models.enums.Theme;
import com.engly.engly_server.repository.UserSettingsRepository;
import com.engly.engly_server.service.common.UserSettingService;
import com.engly.engly_server.utils.cache.CacheName;
import com.engly.engly_server.utils.fieldvalidation.FieldUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new NotFoundException("UserSettings not found"));
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheName.USER_SETTINGS, key = "#id")
    public void update(String id, Boolean notifications, Theme theme) {
        userSettingsRepository.findById(id)
                .ifPresentOrElse(userSettings -> {
                    if (FieldUtil.isValid(theme)) userSettings.setTheme(theme);
                    if (FieldUtil.isValid(notifications)) userSettings.setNotifications(notifications);
                    userSettingsRepository.save(userSettings);
                }, () -> { throw new NotFoundException("User not found"); });
    }
}
