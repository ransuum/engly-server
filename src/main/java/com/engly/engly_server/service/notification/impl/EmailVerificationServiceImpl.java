package com.engly.engly_server.service.notification.impl;


import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.exception.TokenNotFoundException;
import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.dto.EmailSendInfo;
import com.engly.engly_server.models.entity.RefreshToken;
import com.engly.engly_server.models.entity.VerifyToken;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.repo.RefreshTokenRepo;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.repo.VerifyTokenRepo;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.security.jwt.JwtTokenGenerator;
import com.engly.engly_server.service.EmailService;
import com.engly.engly_server.service.impl.EmailMessageGenerator;
import com.engly.engly_server.service.notification.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationServiceImpl implements EmailVerificationService {
    private final VerifyTokenRepo tokenRepo;
    private final EmailService emailService;
    private final EmailMessageGenerator messageGenerator;
    private final UserRepo userRepo;
    private final JwtTokenGenerator generator;
    private final RefreshTokenRepo refreshTokenRepo;
    private final SecurityService service;

    @Value("classpath:emailTemplates/verificationTemplate.txt")
    private Resource messageTemplate;
    @Value("${app.email.notification.check.url}")
    private String urlTemplate;
    @Value("#{'${sysadmin.email}'.split(',\\s*')}")
    private Set<String> sysadminEmails;


    @Override
    public EmailSendInfo sendMessage() {
        final var email = service.getCurrentUserEmail();
        try {
            if (!userRepo.existsByEmail(email))
                throw new NotFoundException("User not found exception email %s".formatted(email));

            final var token = RandomStringUtils.random(32, true, true);
            tokenRepo.save(new VerifyToken(token, email));

            final var message = messageGenerator.generate(Map.of("[email]", email, "[link]", urlTemplate.formatted(token)), messageTemplate);

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
