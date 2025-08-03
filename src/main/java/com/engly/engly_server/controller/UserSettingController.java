package com.engly.engly_server.controller;

import com.engly.engly_server.models.enums.Theme;
import com.engly.engly_server.service.common.UserSettingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-settings")
@Tag(name = "15. User settings", description = "API for configuration settings")
@RequiredArgsConstructor
public class UserSettingController {
    private final UserSettingService userSettingService;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_AUTHORIZE')")
    public ResponseEntity<Void> create(@RequestParam(required = false) Boolean notifications,
                                       @RequestParam(required = false) Theme theme) {
        userSettingService.create(notifications, theme);
        return ResponseEntity.ok().build();
    }


    @PatchMapping
    @PreAuthorize("hasAuthority('SCOPE_AUTHORIZE')")
    public ResponseEntity<Void> update(@RequestParam(required = false) Boolean notifications,
                                       @RequestParam(required = false) Theme theme) {
        userSettingService.update(notifications, theme);
        return ResponseEntity.noContent().build();
    }
}
