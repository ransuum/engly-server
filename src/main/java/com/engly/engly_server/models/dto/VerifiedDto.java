package com.engly.engly_server.models.dto;

public record VerifiedDto(UsersDto usersDto, boolean emailVerified) { }
