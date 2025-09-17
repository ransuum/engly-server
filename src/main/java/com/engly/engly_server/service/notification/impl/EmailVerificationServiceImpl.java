package com.engly.engly_server.service.notification.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.exception.TokenNotFoundException;
import com.engly.engly_server.models.dto.response.AuthResponseDto;
import com.engly.engly_server.models.dto.response.EmailSendInfo;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.repository.UserRepository;
import com.engly.engly_server.repository.VerifyTokenRepository;
import com.engly.engly_server.security.jwt.service.JwtAuthenticationService;
import com.engly.engly_server.service.common.EmailService;
import com.engly.engly_server.service.common.impl.EmailMessageGenerator;
import com.engly.engly_server.service.notification.EmailVerificationService;
import com.engly.engly_server.utils.cache.CacheName;
import com.engly.engly_server.utils.emailsenderconfig.EmailSenderUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final VerifyTokenRepository tokenRepo;
    private final EmailService emailService;
    private final EmailMessageGenerator messageGenerator;
    private final UserRepository userRepository;
    private final JwtAuthenticationService jwtAuthenticationService;

    @Value("classpath:/emailTemplates/verificationTemplate.txt")
    private Resource messageTemplate;
    @Value("${app.email.notification.check.url}")
    private String urlTemplate;

    @Override
    public EmailSendInfo sendMessage(String email) {
        try {
            return new EmailSenderUtil(
                    tokenRepo,
                    messageGenerator,
                    emailService,
                    messageTemplate,
                    urlTemplate,
                    "EmailVerificationServiceImpl:sendMessage")
                    .sendTokenEmail(email, userRepository::existsByEmail, TokenType.EMAIL_VERIFICATION);
        } catch (Exception _) {
            throw new TokenNotFoundException("Token not saved exception email " + email);
        }
    }


    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.USER_PROFILES, allEntries = true),
            @CacheEvict(value = CacheName.USER_ID, allEntries = true),
            @CacheEvict(value = CacheName.ALL_USER, allEntries = true)
    })
    public AuthResponseDto checkToken(String email, String token, HttpServletResponse response) {
        return tokenRepo.findByTokenAndEmail(token, email).map(verifyToken -> {
                    if (!verifyToken.getTokenType().equals(TokenType.EMAIL_VERIFICATION))
                        throw new TokenNotFoundException("Invalid token for email verification");

                    return userRepository.findByEmail(email).map(user -> {
                                user.setEmailVerified(true);
                                user.setRoles("ROLE_USER");

                                tokenRepo.delete(verifyToken);

                                final var userSaved = userRepository.save(user);
                                final var jwtHolder = jwtAuthenticationService.authenticationForVerification(userSaved, response);

                                return new AuthResponseDto(jwtHolder.accessToken(),
                                        12,
                                        TokenType.BEARER,
                                        userSaved.getUsername());
                            })
                            .orElseThrow(() -> new NotFoundException("Invalid User"));
                })
                .orElseThrow(() -> new TokenNotFoundException("Token not found or already verified"));
    }
}
