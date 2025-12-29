package com.engly.engly_server.service.common;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.request.UserSettingsUpdateReq;
import com.engly.engly_server.repository.UserSettingsRepository;
import com.engly.engly_server.utils.CacheName;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.engly.engly_server.exception.handler.ExceptionMessage.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserSettingService {

    private final UserSettingsRepository userSettingsRepository;

    @CacheEvict(value = CacheName.USER_PROFILES, key = "#id")
    public void update(String id, UserSettingsUpdateReq userSettingsUpdateReq) {
        userSettingsRepository.findById(id)
                .ifPresentOrElse(userSettings -> {
                    Optional.ofNullable(userSettingsUpdateReq.theme()).ifPresent(userSettings::setTheme);
                    Optional.ofNullable(userSettingsUpdateReq.notifications()).ifPresent(userSettings::setNotifications);
                    Optional.ofNullable(userSettingsUpdateReq.nativeLanguage()).ifPresent(userSettings::setInterfaceLanguage);
                    userSettingsRepository.save(userSettings);
                }, () -> { throw new NotFoundException(USER_NOT_FOUND); });
    }
}
