package com.github.theborakompanioni.vishy.jdbc;

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
@Import(VishyJdbcConsumerAutoConfig.class)
class VishyJdbcITConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Primary
    @Bean
    public JdbcOpenMrcClientAdapter jdbcOpenMrcClientAdapter(OpenMrcJdbcSaveAction saveAction) {
        return spy(new JdbcOpenMrcClientAdapter(jdbcTemplate, saveAction));
    }

}
