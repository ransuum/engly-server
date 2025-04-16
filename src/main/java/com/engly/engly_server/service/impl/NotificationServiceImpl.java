package com.engly.engly_server.service.impl;


import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.exception.TokenNotFoundException;
import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.dto.EmailSendInfo;
import com.engly.engly_server.models.entity.RefreshToken;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.entity.VerifyToken;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.repo.RefreshTokenRepo;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.repo.VerifyTokenRepo;
import com.engly.engly_server.security.jwt.JwtTokenGenerator;
import com.engly.engly_server.service.EmailService;
import com.engly.engly_server.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final VerifyTokenRepo tokenRepo;
    private final EmailService emailService;
    private final EmailMessageGenerator messageGenerator;
    private final UserRepo userRepo;
    private final JwtTokenGenerator generator;
    private final RefreshTokenRepo refreshTokenRepo;
    private final SecurityService service;

    @Value("#{'${sysadmin.email}'.split(',\\s*')}")
    private Set<String> sysadminEmails;


    @Override
    public EmailSendInfo sendNotifyMessage() {
        var email = service.getCurrentUserEmail();
        try {
            if (!userRepo.existsByEmail(email))
                throw new NotFoundException("User not found exception email %s".formatted(email));

            var token = RandomStringUtils.random(32, true, true);
            tokenRepo.save(new VerifyToken(token, email));

            var message = messageGenerator.generate(token, email);

            emailService.sendEmail(email, message);

            log.info("[NotificationServiceImpl:sendNotifyMessage]Notification was sent for email:{} with token:{}", email, token);
            return new EmailSendInfo(email, "Email sent");
        } catch (Exception e) {
            log.error("[NotificationServiceImpl:sendNotifyMessage]Errors in user:{}", e.getMessage());
            throw new TokenNotFoundException("token not saved exception email %s".formatted(email));
        }
    }


    @Override
    public AuthResponseDto checkToken(String token) {
        var email = service.getCurrentUserEmail();
        var optionalToken = tokenRepo.findByTokenAndEmail(token, email);

        if (optionalToken.isPresent()) {
            VerifyToken verifyToken = optionalToken.get();
            return userRepo.findByEmail(email)
                    .map(user -> {
                        user.setEmailVerified(true);
                        user.setRoles(sysadminEmails.contains(email) ? "ROLE_SYSADMIN" : "ROLE_USER");

                        tokenRepo.delete(verifyToken);

                        final var authentication = generator.createAuthenticationObject(userRepo.save(user));
                        final var accessToken = generator.generateAccessToken(authentication);
                        final var refreshToken = generator.generateRefreshToken(authentication);
                        log.info("[NotificationServiceImpl:checkToken]Token:{} for email:{} was checked and deleted", token, email);

                        final var savedRefreshToken = refreshTokenRepo.save(RefreshToken.builder()
                                .user(user)
                                .refreshToken(refreshToken)
                                .revoked(false)
                                .build());

                        return new AuthResponseDto(accessToken,
                                12,
                                TokenType.Bearer,
                                user.getUsername(),
                                savedRefreshToken.getRefreshToken());
                    })
                    .orElseThrow(() -> new NotFoundException("User not found"));
        }
        throw new TokenNotFoundException("Token not found or already verified");
    }
}
