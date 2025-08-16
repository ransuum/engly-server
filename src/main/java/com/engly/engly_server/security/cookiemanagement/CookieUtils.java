package com.engly.engly_server.security.cookiemanagement;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;

@Slf4j
public record CookieUtils(Cookie[] cookies) {
    public CookieUtils(Cookie[] cookies) {
        this.cookies = cookies == null ? null : cookies.clone();
    }

    public String getRefreshTokenCookie() {
        if (cookies == null) {
            log.debug("No cookies array provided");
            return null;
        }

        log.debug("Searching for 'refreshToken' cookie among {} cookies", cookies.length);

        for (Cookie cookie : cookies) {
            log.debug("Checking cookie: name='{}', value present: {}",
                    cookie.getName(), cookie.getValue() != null);
            if ("refreshToken".equals(cookie.getName())) {
                log.debug("Found refreshToken cookie with value: {}",
                        cookie.getValue() != null ? "present" : "null");
                return cookie.getValue();
            }
        }

        log.debug("refreshToken cookie not found");
        return null;
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