package com.engly.engly_server.models.builder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public record GoogleRedirectUrl(String frontendUrl,
                                String accessToken,
                                String refreshToken,
                                String username) {

    public GoogleRedirectUrl(String frontendUrl, String accessToken, String refreshToken, String username) {
        this.accessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
        this.frontendUrl = frontendUrl;
        this.refreshToken = URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);
        this.username = URLEncoder.encode(username, StandardCharsets.UTF_8);
    }

    public String getRedirectUrl() {
        return String.format("%s/google-auth/callback?access_token=%s&refresh_token=%s&expires_in=%d&token_type=Bearer&username=%s",
                frontendUrl, accessToken, refreshToken, 1800, username);
    }
}
