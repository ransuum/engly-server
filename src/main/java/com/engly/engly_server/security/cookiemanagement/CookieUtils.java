package com.engly.engly_server.security.cookiemanagement;

import com.engly.engly_server.exception.TokenNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;

public record CookieUtils(Cookie[] cookies) {
    public CookieUtils(Cookie[] cookies) {
        this.cookies = cookies == null ? null : cookies.clone();
    }

    public String getRefreshTokenCookie() {
        if (cookies == null) throw new TokenNotFoundException("No cookies found in request");
        return Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new TokenNotFoundException("Refresh token is not found in cookies"));
    }

    public void clearCookies(HttpServletResponse response) {
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