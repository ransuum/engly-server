package com.engly.engly_server.security.jwt.service;

import com.engly.engly_server.exception.InvalidTokenTypeException;
import com.engly.engly_server.exception.TokenGenerationException;
import com.engly.engly_server.models.entity.ChatParticipants;
import com.engly.engly_server.models.entity.RefreshToken;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.repository.ChatParticipantRepository;
import com.engly.engly_server.repository.RefreshTokenRepository;
import com.engly.engly_server.security.jwt.JwtProperties;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class JwtTokenService {
    private final ChatParticipantRepository chatParticipantRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    protected final JwtProperties jwtProperties;

    protected JwtTokenService(ChatParticipantRepository chatParticipantRepository,
                              RefreshTokenRepository refreshTokenRepository, JwtProperties jwtProperties) {
        this.chatParticipantRepository = chatParticipantRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    public final String tokenChooser(Authentication authentication, TokenType tokenType) {
        return switch (tokenType) {
            case REFRESH -> generateRefreshToken(authentication);
            case ACCESS -> generateAccessToken(authentication);
            default -> throw new InvalidTokenTypeException("Token type is invalid");
        };
    }

    protected final JwtClaimsSet.Builder createBaseClaimsBuilder(Authentication authentication) {
        return JwtClaimsSet.builder()
                .id(UUID.randomUUID().toString())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Instant.now())
                .subject(authentication.getName());
    }

    protected final String encodeToken(@NonNull JwtClaimsSet claims, @NonNull JwtEncoder jwtEncoder) {
        try {
            return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        } catch (Exception e) {
            throw new TokenGenerationException("Failed to encode JWT token", e);
        }
    }

    protected final Map<String, String> getRoomRolesForUser(@NonNull String email) {
        final var participants = chatParticipantRepository.findByEmail(email);

        return participants.stream()
                .collect(Collectors.toMap(
                        ChatParticipants::getRoomId,
                        p -> p.getRole().name()
                ));
    }

    public final void saveRefreshToken(Users user, String refreshToken) {
        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plus(jwtProperties.getRefreshTokenValidityDays(), ChronoUnit.DAYS))
                .revoked(false)
                .build());
    }

    public final void createRefreshTokenCookie(@NonNull HttpServletResponse response, String refreshToken) {
        final var cookie = ResponseCookie.from(jwtProperties.getCookie().getName(), refreshToken)
                .maxAge(Duration.ofDays(jwtProperties.getRefreshTokenValidityDays()))
                .path(jwtProperties.getCookie().getPath())
                .httpOnly(jwtProperties.getCookie().isHttpOnly())
                .secure(jwtProperties.getCookie().isSecure())
                .sameSite(jwtProperties.getCookie().getSameSite())
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * Generate access token with user permissions and room roles
     * @param authentication the authentication object
     * @return encoded JWT access token
     */
    protected abstract String generateAccessToken(Authentication authentication);

    /**
     * Generate refresh token for token renewal
     * @param authentication the authentication object
     * @return encoded JWT refresh token
     */
    protected abstract String generateRefreshToken(Authentication authentication);
}
