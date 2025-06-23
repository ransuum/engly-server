package com.engly.engly_server.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

    @Value("${app.backend.url}")
    private String url;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(new Server().url(url)))
                .info(new Info()
                        .title("EnglyChat API")
                        .version("1.0.0")
                        .description("This is the official API documentation for the Engly Server application. " +
                                "It provides endpoints for managing users, chat rooms, messages, and authentication.")
                        .contact(new Contact()
                                .name("Engly Support")
                                .url("https://your-support-page.com")
                                .email("support@engly.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                )
                .tags(List.of(
                        new Tag().name("01. Authentication").description("Endpoints for user sign-up, sign-in, and token management."),
                        new Tag().name("02. Email Verification").description("APIs for verifying a user's email address after registration."),
                        new Tag().name("03. Password Recovery").description("APIs for user password recovery."),
                        new Tag().name("04. User Onboarding").description("APIs for completing user registration, e.g., for social logins."),
                        new Tag().name("05. Public Resources").description("Endpoints that do not require authentication."),
                        new Tag().name("06. Categories").description("APIs for managing room categories."),
                        new Tag().name("07. User Profile").description("APIs for managing the authenticated user's profile."),
                        new Tag().name("08. Messages").description("APIs for retrieving chat messages within a room."),
                        new Tag().name("09. Rooms").description("APIs for creating, managing, and searching for chat rooms."),
                        new Tag().name("10. User Management (Admin)").description("APIs for administrators to manage user accounts."),
                        new Tag().name("11. User validation").description("APIs for user validation operations."),
                        new Tag().name("12. Refresh token admin management").description("APIs for managing refresh tokens by administrators.")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/swagger-ui.html");
    }
}