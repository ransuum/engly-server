package com.engly.engly_server.service;

import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.request.update.ProfileUpdateRequest;

public interface ProfileService {
    UsersDto getProfile();

    UsersDto updateProfile(ProfileUpdateRequest profileUpdateData);
}
