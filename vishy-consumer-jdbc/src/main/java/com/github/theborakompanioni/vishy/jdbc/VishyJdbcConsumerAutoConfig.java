package com.github.theborakompanioni.vishy.jdbc;

import com.codahale.metrics.MetricRegistry;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
@ConditionalOnProperty("vishy.consumer.jdbc.enabled")
public class VishyJdbcConsumerAutoConfig {
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

    @Configuration
    @EnableConfigurationProperties(VishyJdbcProperties.class)
    @EnableTransactionManagement
    public class VishyJdbcConsumerConfig {

        @Autowired
        private VishyJdbcProperties properties;

        @Autowired(required = false)
        private MetricRegistry metricRegistry;

        @Bean
        public HikariConfig  jdbcConsumerHikariConfig() {
            HikariConfig config = new HikariConfig();

            properties.getDriverClassName()
                    .ifPresent(config::setDriverClassName);

            config.setJdbcUrl(properties.getJdbcUrl());
            config.setUsername(properties.getUsername());
            config.setPassword(properties.getPassword());
            config.setPoolName("vishy-consumer-jdbc");

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
        public HikariDataSource jdbcConsumerHikariDataSource() {
            return new HikariDataSource(jdbcConsumerHikariConfig());
        }

        @Bean
        public JdbcTemplate jdbcConsumerJdbcTemplate() {
            return new JdbcTemplate(jdbcConsumerHikariDataSource());
        }

        @Bean(name = "jdbcConsumerTransactionManager")
        public PlatformTransactionManager jdbcConsumerTransactionManager() {
            return new DataSourceTransactionManager(jdbcConsumerHikariDataSource());
        }

        @Bean
        public JdbcOpenMrcClientAdapter jdbcOpenMrcClientAdapter(OpenMrcJdbcSaveAction saveAction) {
            return new JdbcOpenMrcClientAdapter(jdbcConsumerJdbcTemplate(), saveAction);
        }

        @PostConstruct
        public void postConstruct() {
            if (properties.isTableSetupEnabled()) {
                final Flyway flyway = new Flyway();
                flyway.setDataSource(jdbcConsumerHikariDataSource());
                flyway.setLocations(properties.getFlywayScriptsLocation());

                log.info("Starting flyway migration v{}", flyway.getBaselineVersion().getVersion());

                flyway.migrate();
            }
        }
    }
}
