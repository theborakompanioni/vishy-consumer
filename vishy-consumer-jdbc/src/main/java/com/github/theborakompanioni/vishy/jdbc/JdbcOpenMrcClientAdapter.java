package com.github.theborakompanioni.vishy.jdbc;

import com.github.theborakompanioni.openmrc.OpenMrc;
import com.github.theborakompanioni.openmrc.OpenMrcRequestConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import static java.util.Objects.requireNonNull;

@Slf4j
public class JdbcOpenMrcClientAdapter implements OpenMrcRequestConsumer {

    private final JdbcTemplate jdbcTemplate;
    private final OpenMrcJdbcSaveAction saveAction;

    public JdbcOpenMrcClientAdapter(JdbcTemplate jdbcTemplate,
                                    OpenMrcJdbcSaveAction saveAction) {
        this.jdbcTemplate = requireNonNull(jdbcTemplate);
        this.saveAction = requireNonNull(saveAction);
    }

    @Override
    public void accept(OpenMrc.Request request) {
        saveAction.apply(jdbcTemplate, request);
    }
}
