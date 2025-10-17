package com.engly.engly_server.security.config;

import com.engly.engly_server.exception.AuthenticationObjectException;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.security.userconfiguration.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

@Component
public class SecurityContextConfig {

    public Authentication createAndSetAuthenticationAndReturn(Users user, String password) {
        var userDetails = new UserDetailsImpl(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                password,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return auth;
    }

    public void setSecurityContext(Jwt jwt, UserDetails userDetails, HttpServletRequest request) {
        Collection<GrantedAuthority> authorities = new LinkedList<>(userDetails.getAuthorities());

        Optional.ofNullable(jwt.getClaimAsString("scope"))
                .ifPresentOrElse(sc -> authorities.add(new SimpleGrantedAuthority("SCOPE_" + sc.toUpperCase())),
                        () -> {
                            throw new AuthenticationObjectException("You are not authorized to perform this request.");
                        });

        var authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                authorities);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        createAndSetContext(authToken);
    }

    public Authentication createAuthenticationObject(Users users) {
        var userDetails = new UserDetailsImpl(users);
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                users.getPassword(),
                userDetails.getAuthorities());
    }

    public boolean isAuthenticationEmpty() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void createAndSetContext(Authentication authentication) {
        var securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
