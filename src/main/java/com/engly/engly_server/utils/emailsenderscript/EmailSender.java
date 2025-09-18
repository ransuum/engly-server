package com.engly.engly_server.utils.emailsenderscript;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.response.EmailSendInfo;
import com.engly.engly_server.models.entity.VerifyToken;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.repository.VerifyTokenRepository;
import com.engly.engly_server.service.common.EmailService;
import com.engly.engly_server.service.common.impl.EmailMessageGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.Resource;

import java.security.SecureRandom;
import java.util.Map;
import java.util.function.Predicate;

@Slf4j
public record EmailSender(
        VerifyTokenRepository tokenRepo,
        EmailMessageGenerator messageGenerator,
        EmailService emailService,
        Resource messageTemplate,
        String urlTemplate,
        String logTag) {

    public EmailSendInfo sendTokenEmail(String email, Predicate<String> existsChecker, TokenType tokenType) {
        if (!existsChecker.test(email))
            throw new NotFoundException("User not found exception email " + email);

        final var token = generateSecureToken();
        tokenRepo.save(new VerifyToken(token, email, tokenType));

        final var message = messageGenerator.generate(
                Map.of("[email]", email, "[link]", urlTemplate.formatted(token)),
                messageTemplate);

        emailService.sendEmail(email, message);
        log.info("[{}] Message was sent for email:{} with token:{}", logTag, email, token);

        return new EmailSendInfo(email, "Email sent");
    }

    private static String generateSecureToken() {
        return RandomStringUtils.random(32, 0, 0, true, true, null, new SecureRandom());
    }
}
