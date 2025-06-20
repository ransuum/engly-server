package com.engly.engly_server.security.websocket;

import com.engly.engly_server.security.jwt.JwtTokenUtils;
import com.engly.engly_server.security.rsa.RSAKeyRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements HandshakeInterceptor {
    private final JwtTokenUtils jwtTokenUtils;
    private final RSAKeyRecord rsaKeyRecord;

    @Override
    public boolean beforeHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response,
                                   @NotNull WebSocketHandler wsHandler, @NotNull Map<String, Object> attributes) throws Exception {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null) {
                log.error("No authorization token found in WebSocket handshake");
                return false;
            }

            JwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKeyRecord.rsaPublicKey()).build();
            Jwt jwt = jwtDecoder.decode(token);
            String username = jwtTokenUtils.getUserName(jwt);

            UserDetails userDetails = jwtTokenUtils.userDetails(username);
            if (!jwtTokenUtils.isTokenValid(jwt, userDetails)) {
                log.error("Invalid JWT token in WebSocket handshake");
                return false;
            }

            attributes.put("username", username);
            attributes.put("authorities", userDetails.getAuthorities());
            attributes.put("jwt", jwt);

            log.info("WebSocket handshake authenticated for user: {}", username);
            return true;

        } catch (Exception e) {
            log.error("WebSocket authentication failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void afterHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response,
                               @NotNull WebSocketHandler wsHandler, Exception exception) {
        // Nothing to do after handshake
    }

    private String extractTokenFromRequest(ServerHttpRequest request) {
        final List<String> authHeaders = request.getHeaders().get("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.getFirst();
            if (authHeader.startsWith("Bearer ")) return authHeader.substring(7);
        }

        final String query = request.getURI().getQuery();
        if (query != null && query.contains("token=")) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("token=")) return param.substring(6);
            }
        }

        return null;
    }
}
