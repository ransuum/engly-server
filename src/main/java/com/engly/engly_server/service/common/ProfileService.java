package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.response.UsersDto;
import com.engly.engly_server.models.dto.request.ProfileUpdateRequest;

public interface ProfileService {

    UsersDto getProfile(String id);

    UsersDto updateProfile(String userId, ProfileUpdateRequest profileUpdateData);
}
