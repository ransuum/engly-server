package com.engly.engly_server.service.impl;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.entity.AdditionalInfo;
import com.engly.engly_server.models.entity.RefreshToken;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.*;
import com.engly.engly_server.models.request.SignUpRequest;
import com.engly.engly_server.repo.RefreshTokenRepo;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.security.jwt.JwtTokenGenerator;
import com.engly.engly_server.service.AuthService;
import com.engly.engly_server.utils.registration_chooser.RegistrationChooser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.collections.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService, AuthenticationSuccessHandler {
    private final UserRepo userRepo;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final RefreshTokenRepo refreshTokenRepo;
    private final Map<Provider, RegistrationChooser> chooserMap;

    public AuthServiceImpl(UserRepo userRepo, JwtTokenGenerator jwtTokenGenerator,
                           RefreshTokenRepo refreshTokenRepo,
                           List<RegistrationChooser> choosers) {
        this.userRepo = userRepo;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.refreshTokenRepo = refreshTokenRepo;
        this.chooserMap = choosers.stream()
                .collect(Collectors.toMap(RegistrationChooser::getProvider, o -> o));
    }

    @Override
    public AuthResponseDto getJwtTokensAfterAuthentication(Authentication authentication, HttpServletResponse response) {
        try {
            var users = userRepo.findByEmail(authentication.getName())
                    .orElseThrow(() -> {
                        log.error("[AuthService:userSignInAuth] User :{} not found", authentication.getName());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "USER NOT FOUND");
                    });
            users.setLastLogin(Instant.now());
            String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
            String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);

            refreshTokenRepo.save(RefreshToken.builder()
                    .user(users)
                    .refreshToken(refreshToken)
                    .revoked(false)
                    .build());

            jwtTokenGenerator.creatRefreshTokenCookie(response, refreshToken);
            log.info("[AuthService:userSignInAuth] Access token for user:{}, has been generated", users.getUsername());
            return AuthResponseDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .accessTokenExpiry(15 * 60)
                    .username(users.getUsername())
                    .tokenType(TokenType.Bearer)
                    .build();


        } catch (Exception e) {
            log.error("[AuthService:userSignInAuth]Exception while authenticating the user due to :{}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Please Try Again");
        }
    }

    @Override
    public Object getAccessTokenUsingRefreshToken(String authorizationHeader) {
        if (!authorizationHeader.startsWith(TokenType.Bearer.name()))
            return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Please verify your token type");
        final String refreshToken = authorizationHeader.substring(7);

        var refreshTokenEntity = refreshTokenRepo.findByRefreshToken(refreshToken)
                .filter(tokens -> !tokens.isRevoked())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Refresh token revoked"));

        Users users = refreshTokenEntity.getUser();
        Authentication authentication = jwtTokenGenerator.createAuthenticationObject(users);
        String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .accessTokenExpiry(5 * 60)
                .username(users.getUsername())
                .tokenType(TokenType.Bearer)
                .build();
    }

    @Override
    public AuthResponseDto registerUser(SignUpRequest signUpRequest, HttpServletResponse httpServletResponse) {
        try {
            Pair<Users, AdditionalInfo> registration = chooserMap.get(Provider.LOCAL).registration(signUpRequest);

            Authentication authentication = jwtTokenGenerator.createAuthenticationObject(registration.getLeft());


            String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
            String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);
            refreshTokenRepo.save(RefreshToken.builder()
                    .user(registration.getLeft())
                    .refreshToken(refreshToken)
                    .revoked(false)
                    .build());

            log.info("[AuthService:registerUser] User:{} Successfully registered", signUpRequest.username());
            return AuthResponseDto.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiry(5 * 60)
                    .refreshToken(refreshToken)
                    .username(signUpRequest.username())
                    .tokenType(TokenType.Bearer)
                    .build();


        } catch (Exception e) {
            log.error("[AuthService:registerUser]Exception while registering the user due to :{}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String providerId = oauth2User.getAttribute("sub");
        Users user;

        Optional<Users> existingUser = userRepo.findByEmail(email);
        if (existingUser.isPresent()) user = existingUser.get();
         else {
            Pair<Users, AdditionalInfo> additionalInfoPair = chooserMap.get(Provider.GOOGLE)
                    .registration(new SignUpRequest(name, email,
                            "Password123@",
                            EnglishLevels.A1,
                            NativeLanguage.ENGLISH,
                            Goals.DEFAULT,
                            providerId
                    ));

            user = additionalInfoPair.getLeft();
        }

        Authentication userAuth = jwtTokenGenerator.createAuthenticationObject(user);
        String accessToken = jwtTokenGenerator.generateAccessToken(userAuth);
        String refreshToken = jwtTokenGenerator.generateRefreshToken(userAuth);

        refreshTokenRepo.save(RefreshToken.builder()
                .user(user)
                .refreshToken(refreshToken)
                .revoked(false)
                .build());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getWriter(),
                AuthResponseDto.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .accessTokenExpiry(15 * 60)
                        .username(user.getUsername())
                        .tokenType(TokenType.Bearer)
                        .build());
    }
}
