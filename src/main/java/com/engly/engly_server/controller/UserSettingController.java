package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.request.UserSettingsUpdateReq;
import com.engly.engly_server.service.common.UserSettingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-settings")
@Tag(name = "15. User settings", description = "API for configuration settings")
@RequiredArgsConstructor
public class UserSettingController {
    private final UserSettingService userSettingService;

    @PutMapping
    @PreAuthorize("hasAuthority('SCOPE_AUTHORIZE')")
    public ResponseEntity<Void> update(@RequestBody(required = false) UserSettingsUpdateReq userSettingsUpdateReq,
                                       @AuthenticationPrincipal Jwt jwt) {
        userSettingService.update(jwt.getClaim("userId"), userSettingsUpdateReq);
        return ResponseEntity.noContent().build();
    }
}
