package com.engly.engly_server.utils.registrationchooser;

import com.engly.engly_server.models.entity.AdditionalInfo;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Provider;
import com.engly.engly_server.models.dto.create.SignUpRequestDto;
import org.apache.commons.lang3.tuple.Pair;

public sealed interface RegistrationChooser permits EmailRegistration, GoogleRegistration {
    Pair<Users, AdditionalInfo> registration(SignUpRequestDto signUpRequestDto);
    Provider getProvider();
}
