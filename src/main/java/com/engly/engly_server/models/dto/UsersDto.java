package com.engly.engly_server.models.dto;

import java.time.Instant;

public record UsersDto(String id, String firstName, String lastName, String username, String email, Instant createdAt) { }
