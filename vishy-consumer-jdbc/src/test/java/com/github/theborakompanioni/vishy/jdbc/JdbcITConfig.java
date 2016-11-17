package com.github.theborakompanioni.vishy.jdbc;

import com.github.theborakompanioni.openmrc.mapper.OpenMrcJsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.mockito.Mockito.spy;

@Slf4j
@Configuration
@ConditionalOnProperty("vishy.jdbc.enabled")
@EnableConfigurationProperties(JdbcProperties.class)
@EnableTransactionManagement
class JdbcITConfig extends JdbcConfig {

    @Autowired
    private JdbcProperties properties;

    @Override
    @Primary
    @Bean
    public JdbcOpenMrcClientAdapter jdbcOpenMrcClientAdapter(OpenMrcJsonMapper jsonMapper) {
        return spy(new JdbcOpenMrcClientAdapter(properties, jdbcTemplate(), jsonMapper));
    }

    /*
    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            log.info("Starting flyway migration {}",
                    flyway.getBaselineVersion().getVersion());
            flyway.migrate();
        };
    }*/
}
