package com.engly.engly_server.service;

@FunctionalInterface
public interface EmailService {
    void sendEmail(String to, String body);
}
