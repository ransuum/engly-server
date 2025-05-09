package com.engly.engly_server.models.dto.update;

public record PasswordResetRequest(String email, String newPassword, String token) {}
