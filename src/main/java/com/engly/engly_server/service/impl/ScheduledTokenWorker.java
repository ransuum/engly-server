package com.engly.engly_server.service.impl;

import com.engly.engly_server.repo.VerifyTokenRepo;
import com.engly.engly_server.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ScheduledTokenWorker {
    private final VerifyTokenRepo verifyTokenRepo;
    private final EmailService emailService;
    private final EmailMessageGenerator messageGenerator;

    @Scheduled(cron = "0 0 0 * * ?")
    private void deleteExpiredTokens() {
        verifyTokenRepo.deleteAllByDeleteDateLessThanEqual(LocalDateTime.now());
    }

    @Scheduled(cron = "0 0 0 * * ?")
    private void notificateAfter15days() {
        final var now = LocalDateTime.now();
        final var toNotification = verifyTokenRepo.findAllByDeleteDateBetween(now.plusDays(15), now.plusDays(16));
        toNotification.forEach(verifyToken ->
                emailService.sendEmail(verifyToken.getEmail(),
                        messageGenerator.generate(verifyToken.getToken(), verifyToken.getEmail())
                )
        );
    }
}
