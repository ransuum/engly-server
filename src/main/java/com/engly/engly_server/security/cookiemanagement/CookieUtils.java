package com.engly.engly_server.security.cookiemanagement;

import com.engly.engly_server.exception.TokenNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CookieUtils {

    public static String getRefreshTokenCookie(HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new TokenNotFoundException("Refresh token is not found in cookies"));
    }
}
