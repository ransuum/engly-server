package com.engly.engly_server.security.jwt;

import com.engly.engly_server.exception.TokenNotFoundException;
import com.engly.engly_server.security.rsa.RSAKeyRecord;
import com.engly.engly_server.security.userconfiguration.UserDetailsImpl;
import com.engly.engly_server.service.common.UserService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtils {
    private final UserService userService;
    private final JwtDecoder jwtDecoder;

    public JwtTokenUtils(UserService userService, RSAKeyRecord rsaKeyRecord) {
        this.userService = userService;
        this.jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKeyRecord.rsaPublicKey()).build();
    }

    private final Function<Jwt, Pair<UserDetails, Collection<GrantedAuthority>>> tokenValidator = jwt -> {
        final var username = getUserName(jwt);
        final var userDetails = userDetails(username);

        Collection<GrantedAuthority> authorities = Optional.ofNullable(jwt.getClaim("scope"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(scopeStr -> Arrays.stream(scopeStr.split(" "))
                        .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
                        .collect(Collectors.<GrantedAuthority>toList()))
                .orElse(new ArrayList<>());

        authorities.addAll(userDetails.getAuthorities());

        if (!isTokenValid(jwt, userDetails)) throw new TokenNotFoundException("Invalid JWT token");
        return Pair.of(userDetails, authorities);
    };

    public String getUserName(Jwt jwtToken) {
        return jwtToken.getSubject();
    }

    public boolean isTokenValid(Jwt jwtToken, UserDetails userDetails) {
        if (getIfTokenIsExpired(jwtToken)) return false;
        return getUserName(jwtToken).equals(userDetails.getUsername());
    }


    private boolean getIfTokenIsExpired(Jwt jwtToken) {
        return Objects.requireNonNull(jwtToken.getExpiresAt()).isBefore(Instant.now());
    }

    public UserDetails userDetails(String email) {
        return new UserDetailsImpl(userService.findUserEntityByEmail(email));
    }

    public Authentication getAuthenticationFromToken(String jwt) {
        final Jwt token = jwtDecoder.decode(jwt);
        final var pair = tokenValidator.apply(token);
        return new UsernamePasswordAuthenticationToken(pair.getLeft(), null, pair.getRight());
    }
}
