package com.engly.engly_server.security.jwt.service;

import com.engly.engly_server.exception.TokenGenerationException;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.repository.ChatParticipantRepository;
import com.engly.engly_server.repository.RefreshTokenRepository;
import com.engly.engly_server.security.config.AuthenticatedUserProvider;
import com.engly.engly_server.security.jwt.JwtProperties;
import com.engly.engly_server.security.userconfiguration.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
@Slf4j
@NullMarked
public final class JwtTokenServiceImpl extends JwtTokenService {
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final JwtEncoder jwtEncoder;

    public JwtTokenServiceImpl(ChatParticipantRepository chatParticipantRepository,
                               RefreshTokenRepository refreshTokenRepository, AuthenticatedUserProvider authenticatedUserProvider,
                               JwtEncoder jwtEncoder, JwtProperties jwtProperties) {
        super(chatParticipantRepository, refreshTokenRepository, jwtProperties);
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    protected String generateAccessToken(Authentication authentication) {
        try {
            var userDetails = (UserDetailsImpl) authentication.getPrincipal();

            log.info("Generating access token for user: {}", Objects.requireNonNull(userDetails).user().getUsername());

            var claims = createBaseClaimsBuilder(authentication)
                    .expiresAt(Instant.now().plus(jwtProperties.getAccessTokenValidityMinutes(), ChronoUnit.MINUTES))
                    .claim("scope", authenticatedUserProvider.getPermissionsFromRoles(authenticatedUserProvider.getRolesOfUser(authentication)))
                    .claim("roomRoles", getRoomRolesForUser(authentication.getName()))
                    .claim("type", TokenType.ACCESS.name())
                    .claim("roles", authenticatedUserProvider.getRolesOfUser(authentication))
                    .claim("userId", userDetails.user().getId())
                    .build();

            return encodeToken(claims, jwtEncoder);
        } catch (Exception e) {
            throw new TokenGenerationException("Access token generation failed", e);
        }
    }

    @Override
    protected String generateRefreshToken(Authentication authentication) {
        try {
            log.info("Generating refresh token for user: {}", authentication.getName());

            var claims = createBaseClaimsBuilder(authentication)
                    .expiresAt(Instant.now().plus(jwtProperties.getRefreshTokenValidityDays(), ChronoUnit.DAYS))
                    .claim("scope", "REFRESH_TOKEN")
                    .claim("type", TokenType.REFRESH.name())
                    .claim("roles", authenticatedUserProvider.getRolesOfUser(authentication))
                    .build();

            return encodeToken(claims, jwtEncoder);
        } catch (Exception e) {
            throw new TokenGenerationException("Refresh token generation failed", e);
        }
    }
}
