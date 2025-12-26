package com.engly.engly_server.service.common;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.request.UserSettingsUpdateReq;
import com.engly.engly_server.service.mapper.UserSettingsMapper;
import com.engly.engly_server.models.dto.response.UserSettingsDto;
import com.engly.engly_server.models.enums.Theme;
import com.engly.engly_server.repository.UserSettingsRepository;
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
public class UserSettingService {

    private final UserSettingsRepository userSettingsRepository;
    private final UserSettingsMapper userSettingsMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.USER_SETTINGS, key = "#id", sync = true)
    public UserSettingsDto getById(String id) {
        return userSettingsRepository.findById(id)
                .map(userSettingsMapper::toUserSettingsDto)
                .orElseThrow(() -> new NotFoundException(USER_SETTINGS_NOT_FOUND));
    }

    @Transactional
    @CacheEvict(value = CacheName.USER_SETTINGS, key = "#id")
    public void update(String id, UserSettingsUpdateReq userSettingsUpdateReq) {
        userSettingsRepository.findById(id)
                .ifPresentOrElse(userSettings -> {
                    if (userSettingsUpdateReq.theme() != null) userSettings.setTheme(userSettingsUpdateReq.theme());
                    if (userSettingsUpdateReq.notifications() != null)
                        userSettings.setNotifications(userSettingsUpdateReq.notifications());
                    if (userSettingsUpdateReq.nativeLanguage() != null)
                        userSettings.setInterfaceLanguage(userSettingsUpdateReq.nativeLanguage());
                    userSettingsRepository.save(userSettings);
                }, () -> { throw new NotFoundException(USER_NOT_FOUND); });
    }
}
