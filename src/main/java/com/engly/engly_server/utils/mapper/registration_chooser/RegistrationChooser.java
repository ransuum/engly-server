package com.engly.engly_server.utils.mapper.registration_chooser;

import com.engly.engly_server.models.entity.AdditionalInfo;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Provider;
import com.engly.engly_server.models.request.SignUpRequest;
import org.graalvm.collections.Pair;

public interface RegistrationChooser {
    Pair<Users, AdditionalInfo> registration(SignUpRequest signUpRequest);
    Provider getProvider();
}
