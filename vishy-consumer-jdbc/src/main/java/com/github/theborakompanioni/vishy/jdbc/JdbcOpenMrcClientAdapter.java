package com.github.theborakompanioni.vishy.jdbc;

import com.github.theborakompanioni.openmrc.OpenMrc;
import com.github.theborakompanioni.openmrc.OpenMrcRequestConsumer;
import com.github.theborakompanioni.openmrc.json.OpenMrcJsonMapper;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import static java.util.Objects.requireNonNull;

@Slf4j
public class JdbcOpenMrcClientAdapter implements OpenMrcRequestConsumer {

    private final JdbcTemplate jdbcTemplate;
    private final OpenMrcJsonMapper jsonMapper;
    private final String insertSql;

    public JdbcOpenMrcClientAdapter(VishyJdbcProperties properties,
                                    JdbcTemplate jdbcTemplate,
                                    OpenMrcJsonMapper jsonMapper) {
        this.jdbcTemplate = requireNonNull(jdbcTemplate);
        this.jsonMapper = requireNonNull(jsonMapper);

        this.insertSql = createInsertSql(properties);
    }

    @Override
    public void accept(OpenMrc.Request request) {
        final String type = request.getType().name();
        String json = jsonMapper.toJson(request);

        if (log.isDebugEnabled()) {
            log.debug("Persisting via jdbc: {} -> {}", type, json);
        }

        persist(type, json);
    }

    @VisibleForTesting
    void persist(String type, String json) {
        jdbcTemplate.update(insertSql, type, json);
    }

    private String createInsertSql(VishyJdbcProperties properties) {
        return "insert into " +
                properties.getTableName() +
                "(type, json) " +
                "values " +
                "(?,?)";
    }
}
