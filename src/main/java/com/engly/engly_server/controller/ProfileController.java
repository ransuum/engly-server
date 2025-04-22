package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.dto.update.ProfileUpdateRequest;
import com.engly.engly_server.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/check")
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    public ResponseEntity<UsersDto> getProfile() {
        return ResponseEntity.ok(profileService.getProfile());
    }

    @PatchMapping("/update")
    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    public ResponseEntity<UsersDto> updateProfile(@RequestBody ProfileUpdateRequest profileUpdateData) {
        return ResponseEntity.ok(profileService.updateProfile(profileUpdateData));
    }
}
