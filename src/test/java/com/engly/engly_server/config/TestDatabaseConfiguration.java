package com.engly.engly_server.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = TestJpaConfiguration.class)
class TestDatabaseConfiguration extends AbstractTestcontainersConfiguration {

    @Test
    void testDatabaseConfiguration() {
        assertThat(getPostgreSQLContainer().isCreated()).isTrue();
        assertThat(getPostgreSQLContainer().isRunning()).isTrue();
    }
}