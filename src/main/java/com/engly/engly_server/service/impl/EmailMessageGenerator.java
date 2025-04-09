package com.engly.engly_server.service.impl;

import com.engly.engly_server.exception.GenerateTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class EmailMessageGenerator {
    @Value("classpath:messageTemplate.txt")
    private Resource messageTemplate;
    @Value("${app.email.notification.check.url}")
    private String urlTemplate;


    public String generate(String token, String email) {
        String message;
        try {
            message = messageTemplate.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("[NotificationServiceImpl:generateMessage]Error reading message template", e);
            throw new GenerateTokenException("Cannot generate token: ", e);
        }
        message = message.replace("[Ім'я користувача]", email);
        message = message.replace("[Посилання для підтвердження]", urlTemplate.formatted(email, token));
        return message;
    }
}
