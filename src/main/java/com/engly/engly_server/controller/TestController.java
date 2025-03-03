package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.utils.mapper.UserMapper;
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

    public TestController(UserRepo userRepo) {
        this.userRepo = userRepo;
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
}
