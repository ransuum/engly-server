package com.engly.engly_server.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

/**
 * Shared Testcontainers configuration for all integration tests.
 * This class provides a single PostgreSQL container instance that can be reused
 * across multiple test classes to improve performance and resource utilization.
 */
public abstract class AbstractTestcontainersConfiguration {

    @Container
    @ServiceConnection
    @SuppressWarnings("resource") // Container lifecycle is managed by @Container annotation
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    /**
     * Get the shared PostgreSQL container instance.
     * Useful for test classes that need direct access to the container.
     */
    protected static PostgreSQLContainer<?> getPostgreSQLContainer() {
        return postgreSQLContainer;
    }
}