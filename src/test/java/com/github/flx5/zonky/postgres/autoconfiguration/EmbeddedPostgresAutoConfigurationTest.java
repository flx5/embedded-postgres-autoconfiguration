package com.github.flx5.zonky.postgres.autoconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.integration.IntegrationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import reactor.test.StepVerifier;

@SpringBootTest
@Import(MyBuilderCustomizer.class)
@ImportAutoConfiguration(IntegrationAutoConfiguration.class)
class EmbeddedPostgresAutoConfigurationTest {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private MyBuilderCustomizer customizer;

    @Test
    void customizerShouldExecute() {
        assertThat(customizer.isExecuted()).isTrue();
    }

    @Test
    void testJdbcConnection() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            final DatabaseMetaData metaData = connection.getMetaData();
            assertThat(metaData.getDatabaseProductName()).isEqualTo("PostgreSQL");
        }
    }

    @Test
    void testR2dbcConnection() {
        StepVerifier.create(connectionFactory.create())
                .assertNext(connection -> {
                    assertThat(connection.getMetadata().getDatabaseProductName()).isEqualTo("PostgreSQL");
                })
                .verifyComplete();
    }
}
