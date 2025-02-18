package com.engly.engly_server.oauth2;

import com.engly.engly_server.models.entity.RefreshToken;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Provider;
import com.engly.engly_server.models.enums.Roles;
import com.engly.engly_server.repo.RefreshTokenRepo;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.security.jwt.JwtTokenGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

//@Component
//@Slf4j
//public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//    private final JwtTokenGenerator jwtTokenGenerator;
//    private final RefreshTokenRepo refreshTokenRepo;
//    private final UserRepo userRepo;
//
//    public OAuth2LoginSuccessHandler(JwtTokenGenerator jwtTokenGenerator,
//                                     RefreshTokenRepo refreshTokenRepo,
//                                     UserRepo userRepo) {
//        this.jwtTokenGenerator = jwtTokenGenerator;
//        this.refreshTokenRepo = refreshTokenRepo;
//        this.userRepo = userRepo;
//    }
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request,
//                                        HttpServletResponse response,
//                                        Authentication authentication) throws IOException {
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//        String email = oAuth2User.getAttribute("email");
//        String name = oAuth2User.getAttribute("name");
//
//        Users user = userRepo.findByEmail(email)
//                .orElseGet(() -> createNewUser(email, name));
//
//        String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
//        String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);
//
//        refreshTokenRepo.save(RefreshToken.builder()
//                .user(user)
//                .refreshToken(refreshToken)
//                .revoked(false)
//                .build());
//
//        jwtTokenGenerator.creatRefreshTokenCookie(response, refreshToken);
//
//        getRedirectStrategy().sendRedirect(request, response,
//                "http://your-frontend-url/oauth2/redirect?token=" + accessToken);
//    }
//
//    private Users createNewUser(String email, String name) {
//        Users user = Users.builder()
//                .email(email)
//                .username(name)
//                .roles(Roles.ROLE_USER.name())
//                .provider(Provider.GOOGLE)
//                .createdAt(Instant.now())
//                .build();
//
//        return userRepo.save(user);
//    }
//}
