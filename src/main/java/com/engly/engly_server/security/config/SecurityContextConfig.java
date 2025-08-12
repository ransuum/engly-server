package com.engly.engly_server.security.config;

import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.security.userconfiguration.UserDetailsImpl;
import com.engly.engly_server.utils.fieldvalidation.FieldUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedList;

@Component
public class SecurityContextConfig {

    public Authentication createAndSetAuthenticationAndReturn(Users user, String password) {
        final Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(), password, new UserDetailsImpl(user).getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return auth;
    }

    public void setSecurityContext(Jwt jwt, UserDetails userDetails, HttpServletRequest request) {
        Collection<GrantedAuthority> authorities = new LinkedList<>(userDetails.getAuthorities());

        final String scope = jwt.getClaimAsString("scope");
        if (FieldUtil.isValid(scope))
            authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope.toUpperCase()));

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, authorities);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authToken);
        SecurityContextHolder.setContext(securityContext);
    }

    public Authentication createAuthenticationObject(Users users) {
        return new UsernamePasswordAuthenticationToken(users.getEmail(),
                users.getPassword(), new UserDetailsImpl(users).getAuthorities());
    }


    public boolean isSecurityContextEmpty() {
        return SecurityContextHolder.getContext() == null;
    }

    public boolean isAuthenticationEmpty() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }
}
