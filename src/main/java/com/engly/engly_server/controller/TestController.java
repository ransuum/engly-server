package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.entity.RefreshToken;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.models.request.SignUpRequest;
import com.engly.engly_server.repo.RefreshTokenRepo;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.utils.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {
    private final UserRepo userRepo;
    private final RefreshTokenRepo refreshTokenRepo;

    public TestController(UserRepo userRepo, RefreshTokenRepo refreshTokenRepo) {
        this.userRepo = userRepo;
        this.refreshTokenRepo = refreshTokenRepo;
    }

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/default")
    public ResponseEntity<AuthResponseDto> response() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return new ResponseEntity<>(AuthResponseDto.builder()
                .username(authentication.getName())
                .accessToken("token")
                .tokenType(TokenType.Bearer)
                .accessTokenExpiry(25)
                .build(), HttpStatus.OK);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<UsersDto>> getAllUser() {
        return ResponseEntity.ok(userRepo.findAll()
                .stream()
                .map(UserMapper.INSTANCE::toUsersDto)
                .toList());
    }

    @DeleteMapping("/delete/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        userRepo.deleteById(id);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/refresh/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Operation(summary = "Для розробника")
    public ResponseEntity<Long> deleteRefresh(@PathVariable Long id) {
        refreshTokenRepo.deleteById(id);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/refresh-all")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Operation(summary = "Для розробника")
    public ResponseEntity<Long> deleteAll() {
        refreshTokenRepo.deleteAll();
        return ResponseEntity.ok(1L);
    }

    @GetMapping("/refresh-tokens")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Для розробника")
    public ResponseEntity<List<RefreshToken>> getAllRefresh() {
        return ResponseEntity.ok(refreshTokenRepo.findAll());
    }
}
