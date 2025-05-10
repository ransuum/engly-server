package com.engly.engly_server.utils.emailsenderconfig;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.EmailSendInfo;
import com.engly.engly_server.models.entity.VerifyToken;
import com.engly.engly_server.repo.VerifyTokenRepo;
import com.engly.engly_server.service.EmailService;
import com.engly.engly_server.service.impl.EmailMessageGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.Resource;

import java.security.SecureRandom;
import java.util.Map;
import java.util.function.Predicate;

@Slf4j
public record EmailSenderUtil(
        String email,
        VerifyTokenRepo tokenRepo,
        EmailMessageGenerator messageGenerator,
        EmailService emailService,
        Resource messageTemplate,
        String urlTemplate,
        String logTag) {

    public EmailSendInfo sendTokenEmail(Predicate<String> existsChecker) {
        if (!existsChecker.test(email))
            throw new NotFoundException("User not found exception email %s".formatted(email));

        final var token = RandomStringUtils.random(
                32,
                0, 0,
                true, true,
                null,
                new SecureRandom());
        tokenRepo.save(new VerifyToken(token, email));

        final var message = messageGenerator.generate(
                Map.of("[email]", email, "[link]", urlTemplate.formatted(token)), messageTemplate);

        emailService.sendEmail(email, message);
        log.info("[{}] Message was sent for email:{} with token:{}", logTag, email, token);

        return new EmailSendInfo(email, "Email sent");
    }
}
