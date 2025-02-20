package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.request.AdditionalRequestForGoogleUser;
import com.engly.engly_server.service.AdditionalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AdditionalInfoController {
    private final AdditionalService additionalService;

    public AdditionalInfoController(AdditionalService additionalService) {
        this.additionalService = additionalService;
    }

    @PreAuthorize("hasAuthority('SCOPE_ADDITIONAL_INFO')")
    @PostMapping("/additional-info")
    public ResponseEntity<AuthResponseDto> addInfo(@RequestBody AdditionalRequestForGoogleUser additionalRequestForGoogleUser) {
        return new ResponseEntity<>(additionalService.additionalRegistration(additionalRequestForGoogleUser), HttpStatus.CREATED);
    }
}
