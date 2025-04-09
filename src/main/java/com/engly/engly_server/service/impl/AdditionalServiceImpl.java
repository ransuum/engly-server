package com.engly.engly_server.service.impl;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.entity.AdditionalInfo;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.models.request.create.AdditionalRequestForGoogleUser;
import com.engly.engly_server.repo.RefreshTokenRepo;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.security.jwt.JwtTokenGenerator;
import com.engly.engly_server.security.userconfiguration.UserConfig;
import com.engly.engly_server.service.AdditionalService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AdditionalServiceImpl implements AdditionalService {
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserRepo userRepo;
    private final JwtTokenGenerator jwtTokenGenerator;

    @Value("#{'${sysadmin.email}'.split(',\\s*')}")
    private Set<String> sysadminEmails;

    public AdditionalServiceImpl(RefreshTokenRepo refreshTokenRepo, UserRepo userRepo, JwtTokenGenerator jwtTokenGenerator) {
        this.refreshTokenRepo = refreshTokenRepo;
        this.userRepo = userRepo;
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    @Override
    public AuthResponseDto additionalRegistration(AdditionalRequestForGoogleUser additionalRequestForGoogleUser) {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setRoles(sysadminEmails.contains(user.getEmail()) ? "ROLE_SYSADMIN" : "ROLE_USER");

        final var additionalInfo = AdditionalInfo.builder()
                .user(user)
                .goal(additionalRequestForGoogleUser.goals())
                .nativeLanguage(additionalRequestForGoogleUser.nativeLanguage())
                .englishLevel(additionalRequestForGoogleUser.englishLevel())
                .build();
        user.setAdditionalInfo(additionalInfo);
        final var savedUser = userRepo.save(user);

        final Authentication newAuth = new UsernamePasswordAuthenticationToken(savedUser.getEmail(), null,
                new UserConfig(savedUser).getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        final var accessToken = jwtTokenGenerator.generateAccessToken(newAuth);
        final var refreshToken = refreshTokenRepo.save(jwtTokenGenerator
                .createRefreshToken(savedUser, newAuth)).getRefreshToken();

        return new AuthResponseDto(accessToken, 300, TokenType.Bearer, savedUser.getUsername(), refreshToken);
    }
}
