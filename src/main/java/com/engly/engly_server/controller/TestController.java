package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.enums.TokenType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/test")
    public ResponseEntity<AuthResponseDto> response() {
        return new ResponseEntity<>(AuthResponseDto.builder()
                .username("Test")
                .accessToken("token")
                .tokenType(TokenType.Bearer)
                .accessTokenExpiry(25)
                .build(), HttpStatus.OK);
    }
}
