package com.engly.engly_server.security.jwt;

import com.engly.engly_server.models.entity.ChatParticipants;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.repository.ChatParticipantRepository;
import com.engly.engly_server.security.config.SecurityService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenGenerator {
    private final JwtEncoder jwtEncoder;
    private final SecurityService securityService;
    private final ChatParticipantRepository chatParticipantsRepository;

    @Value("${app.backend-cookie.url}")
    private String url;

    public Authentication createAuthenticationObject(Users users) {
        final var roles = users.getRoles();

        final String[] roleArray = roles.split(",");
        final List<GrantedAuthority> authorities = Arrays.stream(roleArray)
                .map(String::trim)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(users.getEmail(), users.getPassword(), authorities);
    }

    public String generateAccessToken(Authentication authentication) {
        final var jti = UUID.randomUUID().toString();
        log.info("[JwtTokenGenerator:generateAccessToken] Token Creation Started for:{}", authentication.getName());
        final var roles = securityService.getRolesOfUser(authentication);
        final var permissions = securityService.getPermissionsFromRoles(roles);

        final var roomRoles = getRoomRolesForUser(authentication.getName());

        final var claims = JwtClaimsSet.builder()
                .id(jti)
                .issuer("chat-engly")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(30, ChronoUnit.MINUTES))
                .subject(authentication.getName())
                .claim("scope", permissions)
                .claim("roomRoles", roomRoles)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public void createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        final int REFRESH_TOKEN_EXPIRE_SECONDS = 25 * 24 * 60 * 60;

        response.setHeader("Set-Cookie",
                String.format(
                        "refreshToken=%s; Max-Age=%d; Path=/; Secure; HttpOnly; SameSite=None; Domain=%s",
                        refreshToken, REFRESH_TOKEN_EXPIRE_SECONDS, url)
        );
    }


    public String generateRefreshToken(Authentication authentication) {
        final var jti = UUID.randomUUID().toString();
        log.info("[JwtTokenGenerator:generateRefreshToken] Token Creation Started for:{}", authentication.getName());

        final var claims = JwtClaimsSet.builder()
                .issuer("chat-engly")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(25, ChronoUnit.DAYS))
                .subject(authentication.getName())
                .claim("scope", "REFRESH_TOKEN")
                .id(jti)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private Map<String, String> getRoomRolesForUser(String email) {
        List<ChatParticipants> participants = chatParticipantsRepository.findActiveParticipantsByUserEmail(email);

        return participants.stream()
                .collect(Collectors.toMap(
                        p -> p.getRoom().getId(),
                        p -> p.getRole().name()
                ));
    }
}
