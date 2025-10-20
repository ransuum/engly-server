package com.engly.engly_server.service.mapper;

import com.engly.engly_server.models.dto.response.NotificationsDto;
import com.engly.engly_server.models.entity.Notifications;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = UserMapper.class)
public interface NotificationMapper {
    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    NotificationsDto toNotificationsDto(Notifications notification);
}
