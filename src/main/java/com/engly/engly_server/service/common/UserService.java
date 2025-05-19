package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.UsersDto;

import java.util.List;

public interface UserService {
    void delete(String id);

    UsersDto findById(String id);

    List<UsersDto> allUsers();

    List<UsersDto> deleteSomeUsers(List<String> ids);
}
