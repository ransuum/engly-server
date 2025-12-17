package com.engly.engly_server.service.mapper;

import com.engly.engly_server.models.dto.response.NotificationsDto;
import com.engly.engly_server.models.entity.Notifications;
import org.jspecify.annotations.NonNull;
import org.mapstruct.Mapper;

@Mapper(uses = UserMapper.class, componentModel = "spring")
public interface NotificationMapper {
    @NonNull NotificationsDto toNotificationsDto(@NonNull Notifications notification);
}
