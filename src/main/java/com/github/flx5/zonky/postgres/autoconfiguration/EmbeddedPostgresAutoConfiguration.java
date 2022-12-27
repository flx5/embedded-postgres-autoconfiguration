package com.github.flx5.zonky.postgres.autoconfiguration;

import java.io.IOException;

import javax.sql.DataSource;

import io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryOptionsBuilderCustomizer;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

/**
 * Auto configuration of an "embedded" postgres database.
 */
@AutoConfiguration(before = R2dbcAutoConfiguration.class)
@ConditionalOnClass(EmbeddedPostgres.class)
@ConfigurationProperties("zonky.autoconfig.postgres")
public class EmbeddedPostgresAutoConfiguration {
    /**
     * Set the postgres data directory. If unset uses a temporary folder.
     */
    private String dataDirectory;

    /**
     * Port for the embedded postgres database. Autodetect free port if zero.
     */
    private int port;

    /**
     * Enable cleaning of the data directory on start.
     */
    private boolean cleanDataDirectory = true;

    /**
     * Embedded Postgres wrapper.
     *
     * @return The wrapper.
     * @throws IOException The server could not be started.
     */
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    EmbeddedPostgres embeddedPostgres() throws IOException {
        final EmbeddedPostgres.Builder builder = EmbeddedPostgres.builder()
                .setCleanDataDirectory(cleanDataDirectory)
                .setPort(port);

        if (StringUtils.hasText(dataDirectory)) {
            builder.setDataDirectory(dataDirectory);
        }

        return builder.start();
    }

    /**
     * Datasource configured to use the embedded database.
     *
     * @param embedded The embedded database.
     * @return JDBC datasource.
     */
    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource(EmbeddedPostgres embedded) {
        return embedded.getPostgresDatabase();
    }

    /**
     * Configure r2dbc factory to use the embedded database.
     *
     * @param embedded The embedded database.
     * @param customizers The factory customizers.
     * @return The configured factory.
     */
    @Bean
    @ConditionalOnMissingBean
    public ConnectionFactory connectionFactory(EmbeddedPostgres embedded, ObjectProvider<ConnectionFactoryOptionsBuilderCustomizer> customizers) {
        final ConnectionFactoryOptions.Builder optionBuilder = ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, PostgresqlConnectionFactoryProvider.POSTGRESQL_DRIVER)
                .option(ConnectionFactoryOptions.HOST, "localhost")
                .option(ConnectionFactoryOptions.PORT, embedded.getPort());

        PGSimpleDataSource pgDataSource = (PGSimpleDataSource) embedded.getPostgresDatabase();

        if (pgDataSource.getUser() != null) {
            optionBuilder.option(ConnectionFactoryOptions.USER, pgDataSource.getUser());
        }

        if (pgDataSource.getDatabaseName() != null) {
            optionBuilder.option(ConnectionFactoryOptions.DATABASE, pgDataSource.getDatabaseName());
        }

        for (ConnectionFactoryOptionsBuilderCustomizer customizer : customizers) {
            customizer.customize(optionBuilder);
        }

        final ConnectionFactoryBuilder connectionFactoryBuilder = ConnectionFactoryBuilder.withOptions(optionBuilder);
        return connectionFactoryBuilder.build();
    }

    public String getDataDirectory() {
        return dataDirectory;
    }

    public void setDataDirectory(String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isCleanDataDirectory() {
        return cleanDataDirectory;
    }

    public void setCleanDataDirectory(boolean cleanDataDirectory) {
        this.cleanDataDirectory = cleanDataDirectory;
    }
}
