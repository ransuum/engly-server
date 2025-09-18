package com.engly.engly_server.security.cookiemanagement;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;

public record CookieUtils(Cookie[] cookies) {

    public CookieUtils(Cookie[] cookies) {
        this.cookies = cookies == null ? null : cookies.clone();
    }

    public @Nullable String getRefreshTokenCookie() {
        if (cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    public void clearCookies(@NonNull HttpServletResponse response) {
        final var cookie = ResponseCookie.from("refreshToken", "deleted")
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final CookieUtils that = (CookieUtils) o;
        return Arrays.equals(cookies(), that.cookies());
    }

    @Override
    public int hashCode() {
        return 31 * Arrays.hashCode(cookies);
    }

    @Override
    public @NonNull String toString() {
        return "CookieUtils{" +
                "cookies=" + Arrays.toString(cookies) +
                '}';
    }
}