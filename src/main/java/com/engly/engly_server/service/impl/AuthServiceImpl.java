package com.engly.engly_server.service.impl;

import com.engly.engly_server.models.dto.AuthResponseDto;
import com.engly.engly_server.models.entity.AdditionalInfo;
import com.engly.engly_server.models.entity.RefreshToken;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.Provider;
import com.engly.engly_server.models.enums.TokenType;
import com.engly.engly_server.models.request.SignUpRequest;
import com.engly.engly_server.repo.RefreshTokenRepo;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.security.jwt.JwtTokenGenerator;
import com.engly.engly_server.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepo;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final RefreshTokenRepo refreshTokenRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepo userRepo, JwtTokenGenerator jwtTokenGenerator,
                           RefreshTokenRepo refreshTokenRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.refreshTokenRepo = refreshTokenRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponseDto getJwtTokensAfterAuthentication(Authentication authentication, HttpServletResponse response) {
        try {
            var users = userRepo.findByEmail(authentication.getName())
                    .orElseThrow(() -> {
                        log.error("[AuthService:userSignInAuth] User :{} not found", authentication.getName());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "USER NOT FOUND");
                    });

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
            log.info("[AuthService:registerUser]User Registration Started with :::{}", signUpRequest);
            userRepo.findByEmail(signUpRequest.email()).ifPresent(users -> {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Already Exist");
            });

            var users = Users.builder()
                    .roles("ROLE_USER")
                    .createdAt(Instant.now())
                    .email(signUpRequest.email())
                    .username(signUpRequest.username())
                    .password(passwordEncoder.encode(signUpRequest.password()))
                    .provider(Provider.LOCAL)
                    .build();

            var addInfo = AdditionalInfo.builder()
                    .goals(signUpRequest.goals())
                    .englishLevel(signUpRequest.englishLevel())
                    .gender(signUpRequest.gender())
                    .dateOfBirth(signUpRequest.dateOfBirth())
                    .nativeLanguage(signUpRequest.nativeLanguage())
                    .build();

            users.setAdditionalInfo(addInfo);
            addInfo.setUsers(users);

            var savedUser = userRepo.save(users);

            Authentication authentication = jwtTokenGenerator.createAuthenticationObject(savedUser);


            String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
            String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);
            refreshTokenRepo.save(RefreshToken.builder()
                    .user(users)
                    .refreshToken(refreshToken)
                    .revoked(false)
                    .build());

            log.info("[AuthService:registerUser] User:{} Successfully registered", signUpRequest.username());
            return AuthResponseDto.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiry(5 * 60)
                    .username(signUpRequest.username())
                    .tokenType(TokenType.Bearer)
                    .build();


        } catch (Exception e) {
            log.error("[AuthService:registerUser]Exception while registering the user due to :{}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
