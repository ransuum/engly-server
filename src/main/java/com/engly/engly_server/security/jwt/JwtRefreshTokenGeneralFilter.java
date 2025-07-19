package com.engly.engly_server.security.jwt;

import com.engly.engly_server.repo.RefreshTokenRepo;
import com.engly.engly_server.security.cookiemanagement.CookieUtils;
import com.engly.engly_server.security.rsa.RSAKeyRecord;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtRefreshTokenGeneralFilter extends JwtGeneralFilter {
    private final RefreshTokenRepo refreshTokenRepo;

    public JwtRefreshTokenGeneralFilter(RSAKeyRecord rsaKeyRecord,
                                        JwtTokenUtils jwtTokenUtils,
                                        RefreshTokenRepo refreshTokenRepo) {
        super(rsaKeyRecord, jwtTokenUtils);
        this.refreshTokenRepo = refreshTokenRepo;
    }

    @Override
    protected String extractToken(HttpServletRequest request) {
        return new CookieUtils(request.getCookies()).getRefreshTokenCookie();
    }

    @Override
    protected boolean isTokenValidInContext(Jwt jwt) {
        return refreshTokenRepo.findByTokenAndRevokedIsFalse(jwt.getTokenValue())
                .isPresent();
    }
}
