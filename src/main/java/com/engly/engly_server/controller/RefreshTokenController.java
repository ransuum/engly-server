package com.engly.engly_server.controller;

import com.engly.engly_server.repo.RefreshTokenRepo;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/refresh-token")
@Tag(name = "12. Refresh token admin management", description = "APIs for managing refresh tokens by administrators.")
public class RefreshTokenController {
    private final RefreshTokenRepo refreshTokenRepo;

    public RefreshTokenController(RefreshTokenRepo refreshTokenRepo) {
        this.refreshTokenRepo = refreshTokenRepo;
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public void deleteAllTokens() {
        refreshTokenRepo.deleteAll();
    }
}
