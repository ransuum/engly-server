package com.engly.engly_server.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Minimal Spring Boot configuration for database tests.
 * This configuration excludes security-related beans and focuses only on JPA/database functionality.
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@EntityScan("com.engly.engly_server.models.entity")
@EnableJpaRepositories("com.engly.engly_server.repository")
public class TestJpaConfiguration {
}