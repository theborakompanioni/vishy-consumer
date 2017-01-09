package com.github.theborakompanioni.vishy.jdbc;

import com.github.theborakompanioni.openmrc.json.OpenMrcJsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.spy;

@Slf4j
@Configuration
@Import(VishyJdbcConsumerConfig.class)
class VishyJdbcITConfig {

    private final VishyJdbcProperties properties;
    private final JdbcTemplate jdbcTemplate;
    private final OpenMrcJsonMapper jsonMapper;

    @Autowired
    public VishyJdbcITConfig(VishyJdbcProperties properties, JdbcTemplate jdbcTemplate, OpenMrcJsonMapper jsonMapper) {
        this.properties = properties;
        this.jdbcTemplate = jdbcTemplate;
        this.jsonMapper = jsonMapper;
    }

    @Primary
    @Bean
    public JdbcOpenMrcClientAdapter jdbcOpenMrcClientAdapter(JdbcTemplate jdbcTemplate, OpenMrcJsonMapper jsonMapper) {
        return spy(new JdbcOpenMrcClientAdapter(properties, jdbcTemplate, jsonMapper));
    }
}
