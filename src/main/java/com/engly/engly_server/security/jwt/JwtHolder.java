package com.engly.engly_server.security.jwt;

import org.jspecify.annotations.NonNull;

public record JwtHolder(@NonNull String refreshToken, @NonNull String accessToken) { }
