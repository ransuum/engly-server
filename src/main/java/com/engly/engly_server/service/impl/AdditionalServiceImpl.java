package com.engly.engly_server.service.impl;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.entity.AdditionalInfo;
import com.engly.engly_server.models.entity.RefreshToken;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.models.request.AdditionalRequestForGoogleUser;
import com.engly.engly_server.repo.RefreshTokenRepo;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.security.jwt.JwtTokenGenerator;
import com.engly.engly_server.security.user_configuration.UserConfig;
import com.engly.engly_server.service.AdditionalService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdditionalServiceImpl implements AdditionalService {
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserRepo userRepo;
    private final JwtTokenGenerator jwtTokenGenerator;

    public AdditionalServiceImpl(RefreshTokenRepo refreshTokenRepo, UserRepo userRepo, JwtTokenGenerator jwtTokenGenerator) {
        this.refreshTokenRepo = refreshTokenRepo;
        this.userRepo = userRepo;
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    @Override
    public AuthResponseDto additionalRegistration(AdditionalRequestForGoogleUser additionalRequestForGoogleUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Users user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setRoles("ROLE_USER");

        AdditionalInfo additionalInfo = AdditionalInfo.builder()
                .user(user)
                .goal(additionalRequestForGoogleUser.goals())
                .nativeLanguage(additionalRequestForGoogleUser.nativeLanguage())
                .gender(additionalRequestForGoogleUser.gender())
                .englishLevel(additionalRequestForGoogleUser.englishLevel())
                .dateOfBirth(additionalRequestForGoogleUser.dateOfBirth())
                .build();

        user.setAdditionalInfo(additionalInfo);

        Users save = userRepo.save(user);

        Authentication newAuth = new UsernamePasswordAuthenticationToken(save.getEmail(), null,
                new UserConfig(save).getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        String accessToken = jwtTokenGenerator.generateAccessToken(newAuth);
        String refreshToken = jwtTokenGenerator.generateRefreshToken(newAuth);

        refreshTokenRepo.save(RefreshToken.builder()
                .user(save)
                .refreshToken(refreshToken)
                .revoked(false)
                .build());

        return new AuthResponseDto(accessToken, 300, TokenType.Bearer, save.getUsername(), refreshToken);
    }
}
