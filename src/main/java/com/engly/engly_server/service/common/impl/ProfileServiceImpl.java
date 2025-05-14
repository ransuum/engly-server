package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.dto.VerifiedDto;
import com.engly.engly_server.models.dto.update.ProfileUpdateRequest;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.service.common.ProfileService;
import com.engly.engly_server.mapper.UserMapper;
import com.engly.engly_server.utils.fieldvalidation.FieldUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.engly.engly_server.utils.fieldvalidation.FieldUtil.isValid;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserRepo userRepo;

    @Override
    @Transactional(readOnly = true)
    public Object getProfile() {
        final var email = SecurityContextHolder.getContext().getAuthentication().getName();
        final var user = UserMapper.INSTANCE.toUsersDto(userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User Not Found")));
        return user.roles().equals("ROLE_NOT_VERIFIED") ?
                new VerifiedDto(user, false) : new VerifiedDto(user, true);
    }

    @Override
    public UsersDto updateProfile(ProfileUpdateRequest profileUpdateData) {
        final var email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email)
                .map(user -> {
                    if (isValid(profileUpdateData.username())) user.setUsername(profileUpdateData.username());
                    if (isValid(profileUpdateData.goal())) user.getAdditionalInfo().setGoal(profileUpdateData.goal());
                    if (isValid(profileUpdateData.englishLevel()))
                        user.getAdditionalInfo().setEnglishLevel(profileUpdateData.englishLevel());
                    if (isValid(profileUpdateData.nativeLanguage()))
                        user.getAdditionalInfo().setNativeLanguage(profileUpdateData.nativeLanguage());

                    return UserMapper.INSTANCE.toUsersDto(userRepo.save(user));
                })
                .orElseThrow(() -> new NotFoundException("User Not Found"));
    }
}
