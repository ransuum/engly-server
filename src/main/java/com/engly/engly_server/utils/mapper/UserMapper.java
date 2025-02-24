package com.engly.engly_server.utils.mapper;

import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UsersDto toUsersDto(Users user);
}
