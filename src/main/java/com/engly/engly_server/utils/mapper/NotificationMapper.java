package com.engly.engly_server.utils.mapper;

import com.engly.engly_server.models.dto.NotificationsDto;
import com.engly.engly_server.models.entity.Notifications;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NotificationMapper {
    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    NotificationsDto toNotificationsDto(Notifications notification);
}
