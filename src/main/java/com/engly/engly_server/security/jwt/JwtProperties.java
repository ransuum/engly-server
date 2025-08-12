package com.engly.engly_server.security.jwt;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "jwt-props")
@Data
@Validated
public class JwtProperties {
    @NotNull
    @Min(1)
    private Integer refreshTokenValidityDays = 25;

    @NotNull
    @Min(1)
    private Integer accessTokenValidityMinutes = 30;

    @NotBlank
    private String issuer = "chat-engly";

    @Valid
    private Cookie cookie = new Cookie();

    @Data
    public static class Cookie {
        @NotBlank
        private String name = "refreshToken";

        @NotBlank
        private String path = "/";

        private boolean httpOnly = true;
        private boolean secure = true;

        @NotBlank
        private String sameSite = "None";
    }
}
