package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.response.UsersDto;
import com.engly.engly_server.models.dto.request.ProfileUpdateRequest;

public interface ProfileService {

    String NOT_FOUND_PROFILE = "Cannot found this profile";

    UsersDto getProfile();

    UsersDto updateProfile(ProfileUpdateRequest profileUpdateData);
}
