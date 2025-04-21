package com.engly.engly_server.service.impl;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.entity.AdditionalInfo;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.models.request.create.AdditionalRequestForGoogleUser;
import com.engly.engly_server.repo.RefreshTokenRepo;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.security.jwt.JwtTokenGenerator;
import com.engly.engly_server.security.userconfiguration.UserDetailsImpl;
import com.engly.engly_server.service.AdditionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdditionalServiceImpl implements AdditionalService {
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserRepo userRepo;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final SecurityService securityService;

    @Value("#{'${sysadmin.email}'.split(',\\s*')}")
    private Set<String> sysadminEmails;

    @Override
    public AuthResponseDto additionalRegistration(AdditionalRequestForGoogleUser additionalRequestForGoogleUser) {
        final var email = securityService.getCurrentUserEmail();
        return userRepo.findByEmail(email)
                .map(user -> {
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
                            new UserDetailsImpl(savedUser).getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(newAuth);

                    final var accessToken = jwtTokenGenerator.generateAccessToken(newAuth);
                    final var refreshToken = refreshTokenRepo.save(jwtTokenGenerator
                            .createRefreshToken(savedUser, newAuth)).getRefreshToken();

                    return new AuthResponseDto(accessToken, 300, TokenType.Bearer, savedUser.getUsername(), refreshToken);
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
