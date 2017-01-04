package com.github.theborakompanioni.vishy.jdbc;

import com.codahale.metrics.MetricRegistry;
import com.github.theborakompanioni.openmrc.json.OpenMrcJsonMapper;
import com.github.theborakompanioni.openmrc.json.StandardOpenMrcJsonMapper;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.ExtensionRegistry;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Optional;

@Slf4j
@Configuration
@ConditionalOnProperty("vishy.jdbc.enabled")
@EnableConfigurationProperties(VishyJdbcProperties.class)
@EnableTransactionManagement
public class VishyJdbcConfig {
    /**
     * Reasons for static declaration: created very early in the application’s lifecycle
     * allows the bean to be created without having to instantiate the @Configuration class
     * <p>
     * https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-validation
     */
    @Bean
    public static VishyJdbcPropertiesValidator configurationPropertiesValidator() {
        return new VishyJdbcPropertiesValidator();
    }

    @Autowired
    private VishyJdbcProperties properties;

    @Autowired(required = false)
    private MetricRegistry metricRegistry;

    @Bean
    public HikariConfig hikariConfig() {
        HikariConfig config = new HikariConfig();

        properties.getDriverClassName()
                .ifPresent(config::setDriverClassName);

        config.setJdbcUrl(properties.getJdbcUrl());
        config.setUsername(properties.getUsername());
        config.setPassword(properties.getPassword());

        if (metricRegistry != null) {
            config.setMetricRegistry(metricRegistry);
        }

        // TODO: make configurable
        config.addDataSourceProperty("cachePrepStmts", String.valueOf(true));
        config.addDataSourceProperty("prepStmtCacheSize", String.valueOf(250));
        config.addDataSourceProperty("prepStmtCacheSqlLimit", String.valueOf(2048));

        return config;
    }

    @Bean
    public HikariDataSource hikariDataSource() {
        return new HikariDataSource(hikariConfig());
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(hikariDataSource());
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(hikariDataSource());
    }

    @Bean
    public JdbcOpenMrcClientAdapter jdbcOpenMrcClientAdapter(OpenMrcJsonMapper openMrcJsonMapper) {
        return new JdbcOpenMrcClientAdapter(properties, jdbcTemplate(), openMrcJsonMapper);
    }

    @Bean
    @ConditionalOnMissingBean(OpenMrcJsonMapper.class)
    public OpenMrcJsonMapper standardOpenMrcJsonMapper(Optional<ExtensionRegistry> extensionRegistry,
                                                       Optional<MetricRegistry> metricRegistry) {
        return new StandardOpenMrcJsonMapper(
                extensionRegistry.orElseGet(ExtensionRegistry::getEmptyRegistry),
                metricRegistry.orElseGet(MetricRegistry::new));
    }

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        if (!properties.isTableSetupEnabled()) {
            return idleFlywayMigrationStrategy();
        }

        return initializingFlywayMigrationStrategy();
    }

    private FlywayMigrationStrategy initializingFlywayMigrationStrategy() {
        return flyway -> {
            log.info("Starting flyway migration v{}", flyway.getBaselineVersion().getVersion());

            flyway.setTable("vishy_schema_version");
            flyway.setDataSource(hikariDataSource());
            flyway.setPlaceholders(ImmutableMap.<String, String>builder()
                    .put("vishy.table.name", properties.getTableName())
                    .build());

            flyway.migrate();
        };
    }

    private FlywayMigrationStrategy idleFlywayMigrationStrategy() {
        return flyway -> log.info("Skipping flyway migration - vishy.");
    }
}
