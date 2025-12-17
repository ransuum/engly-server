package com.engly.engly_server.service.mapper;

import com.engly.engly_server.models.dto.response.UserWhoReadsMessageDto;
import com.engly.engly_server.models.dto.response.UsersDto;
import com.engly.engly_server.models.entity.Users;
import org.jspecify.annotations.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @NonNull UsersDto toUsersDto(@NonNull Users user);

    @NonNull UserWhoReadsMessageDto toWhoReadMessage(@NonNull Users user);
}
