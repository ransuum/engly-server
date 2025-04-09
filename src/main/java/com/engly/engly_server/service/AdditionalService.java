package com.engly.engly_server.service;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.request.create.AdditionalRequestForGoogleUser;

@FunctionalInterface
public interface AdditionalService {
    AuthResponseDto additionalRegistration(AdditionalRequestForGoogleUser additionalRequestForGoogleUser);
}
