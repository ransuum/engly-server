package com.engly.engly_server.service.notification.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.exception.TokenNotFoundException;
import com.engly.engly_server.models.dto.response.AuthResponseDto;
import com.engly.engly_server.models.dto.response.EmailSendInfo;
import com.engly.engly_server.models.dto.request.PasswordResetRequest;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.repository.UserRepository;
import com.engly.engly_server.repository.VerifyTokenRepository;
import com.engly.engly_server.security.jwt.service.JwtAuthenticationService;
import com.engly.engly_server.service.common.EmailService;
import com.engly.engly_server.service.common.impl.EmailMessageGenerator;
import com.engly.engly_server.service.notification.PasswordResetService;
import com.engly.engly_server.utils.emailsenderscript.EmailSender;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {

    private final VerifyTokenRepository tokenRepo;
    private final EmailService emailService;
    private final EmailMessageGenerator messageGenerator;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationService jwtAuthenticationService;

    @Value("classpath:/emailTemplates/password-resetTemplate.txt")
    private Resource messageTemplate;
    @Value("${app.email.notification.password-reset.url}")
    private String urlTemplate;


    @Override
    public EmailSendInfo sendMessage(String email) {
        try {
            return new EmailSender(
                    tokenRepo,
                    messageGenerator,
                    emailService,
                    messageTemplate,
                    urlTemplate,
                    "PasswordResetServiceImpl:sendMessage")
                    .sendTokenEmail(email, userRepository::existsByEmail, TokenType.PASSWORD_RESET);
        } catch (Exception _) {
            throw new TokenNotFoundException("token not saved exception email %s".formatted(email));
        }
    }

    @Override
    @Transactional
    public AuthResponseDto passwordReset(PasswordResetRequest data, HttpServletResponse response) {
        return tokenRepo.findById(data.token()).map(verifyToken -> {
                    if (!verifyToken.getTokenType().equals(TokenType.PASSWORD_RESET))
                        throw new TokenNotFoundException("Invalid token for password reset");

                    return userRepository.findByEmail(verifyToken.getEmail()).map(user -> {
                                user.setPassword(passwordEncoder.encode(data.newPassword()));
                                if (Boolean.FALSE.equals(user.getEmailVerified())) {
                                    user.setEmailVerified(true);
                                    user.setRoles("ROLE_USER");
                                }

                                tokenRepo.delete(verifyToken);

                                var jwtHolder = jwtAuthenticationService.authentication(user, response);

                                return new AuthResponseDto(jwtHolder.accessToken(),
                                        12,
                                        TokenType.BEARER,
                                        user.getUsername());
                            })
                            .orElseThrow(() -> new NotFoundException("User not found"));
                })
                .orElseThrow(() -> new TokenNotFoundException("Token not found or already verified"));
    }
}


