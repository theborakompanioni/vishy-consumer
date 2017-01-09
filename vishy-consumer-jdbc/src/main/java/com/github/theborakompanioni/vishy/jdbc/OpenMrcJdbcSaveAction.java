package com.github.theborakompanioni.vishy.jdbc;

import com.github.theborakompanioni.openmrc.OpenMrc;
import org.springframework.jdbc.core.JdbcTemplate;

public interface OpenMrcJdbcSaveAction {
    void apply(JdbcTemplate jdbcTemplate, OpenMrc.Request request);
}
