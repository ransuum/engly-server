package com.engly.engly_server.security.jwt;

import org.jspecify.annotations.Nullable;

public record JwtHolder(String refreshToken, @Nullable String accessToken) { }
