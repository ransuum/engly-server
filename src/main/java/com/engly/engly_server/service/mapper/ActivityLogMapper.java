package com.engly.engly_server.service.mapper;

import com.engly.engly_server.models.dto.response.ActivityLogsDto;
import com.engly.engly_server.models.entity.ActivityLogs;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = UserMapper.class)
public interface ActivityLogMapper {
    ActivityLogMapper INSTANCE = Mappers.getMapper(ActivityLogMapper.class);

    ActivityLogsDto toDto(ActivityLogs activityLogs);
}
