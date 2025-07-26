package com.engly.engly_server.models.dto;

import com.engly.engly_server.security.jwt.JwtHolder;

public record LoginGoogleResult(JwtHolder jwtHolder, AuthResponseDto authResponseDto) { }
