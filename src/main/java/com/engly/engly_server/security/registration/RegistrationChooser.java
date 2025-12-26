package com.engly.engly_server.security.registration;

import com.engly.engly_server.models.dto.request.AuthRequest;
import com.engly.engly_server.models.entity.UserSettings;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.NativeLanguage;
import com.engly.engly_server.models.enums.Provider;
import com.engly.engly_server.models.enums.Theme;
import org.jspecify.annotations.NonNull;

public sealed interface RegistrationChooser permits EmailRegistration, GoogleRegistration {

    Users registration(AuthRequest.@NonNull SignUpRequest signUpRequestDto);

    Provider getProvider();

    default UserSettings buildUserSettings(Users users) {
        NativeLanguage nativeLanguage = users.getAdditionalInfo().getNativeLanguage();
        return UserSettings.builder()
                .user(users)
                .theme(Theme.BRIGHT)
                .interfaceLanguage(nativeLanguage)
                .notifications(true)
                .build();
    }
}
