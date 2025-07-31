package com.engly.engly_server.security.registration;

import com.engly.engly_server.models.dto.create.SignUpRequest;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Provider;

public sealed interface RegistrationChooser permits EmailRegistration, GoogleRegistration {
    Users registration(SignUpRequest signUpRequestDto);

    Provider getProvider();
}
