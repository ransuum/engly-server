package com.engly.engly_server.security.jwt.service.impl;

import com.engly.engly_server.exception.TokenGenerationException;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.repository.ChatParticipantRepository;
import com.engly.engly_server.repository.RefreshTokenRepository;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.security.jwt.JwtProperties;
import com.engly.engly_server.security.jwt.service.JwtTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class JwtTokenServiceImpl extends JwtTokenService {
    private final SecurityService securityService;
    private final JwtEncoder jwtEncoder;

    public JwtTokenServiceImpl(ChatParticipantRepository chatParticipantRepository,
                               RefreshTokenRepository refreshTokenRepository, SecurityService securityService,
                               JwtEncoder jwtEncoder, JwtProperties jwtProperties) {
        super(chatParticipantRepository, refreshTokenRepository, jwtProperties);
        this.securityService = securityService;
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    protected String generateAccessToken(Authentication authentication) {
        try {
            log.debug("Generating access token for user: {}", authentication.getName());

            final var permissions = securityService.getPermissionsFromRoles(
                    securityService.getRolesOfUser(authentication));
            final var roomRoles = getRoomRolesForUser(authentication.getName());

            final var claims = createBaseClaimsBuilder(authentication)
                    .expiresAt(Instant.now().plus(jwtProperties.getAccessTokenValidityMinutes(), ChronoUnit.MINUTES))
                    .claim("scope", permissions)
                    .claim("roomRoles", roomRoles)
                    .claim("type", TokenType.ACCESS.name())
                    .build();

            return encodeToken(claims, jwtEncoder);
        } catch (Exception e) {
            log.error("Failed to generate access token for user: {}", authentication.getName(), e);
            throw new TokenGenerationException("Access token generation failed", e);
        }
    }

    @Override
    protected String generateRefreshToken(Authentication authentication) {
        try {
            log.debug("Generating refresh token for user: {}", authentication.getName());

            final var claims = createBaseClaimsBuilder(authentication)
                    .expiresAt(Instant.now().plus(jwtProperties.getRefreshTokenValidityDays(), ChronoUnit.DAYS))
                    .claim("scope", "REFRESH_TOKEN")
                    .claim("type", TokenType.REFRESH.name())
                    .build();

            return encodeToken(claims, jwtEncoder);
        } catch (Exception e) {
            log.error("Failed to generate refresh token for user: {}", authentication.getName(), e);
            throw new TokenGenerationException("Refresh token generation failed", e);
        }
    }
}
