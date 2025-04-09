package com.engly.engly_server.utils.registrationchooser;

import com.engly.engly_server.models.entity.AdditionalInfo;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Provider;
import com.engly.engly_server.models.request.create.SignUpRequest;
import org.apache.commons.lang3.tuple.Pair;

public interface RegistrationChooser {
    Pair<Users, AdditionalInfo> registration(SignUpRequest signUpRequest);
    Provider getProvider();
}
