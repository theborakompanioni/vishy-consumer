package com.github.theborakompanioni.vishy.jdbc;

import com.codahale.metrics.MetricRegistry;
import com.github.theborakompanioni.openmrc.OpenMrc;
import com.github.theborakompanioni.openmrc.json.OpenMrcJsonMapper;
import com.github.theborakompanioni.openmrc.json.StandardOpenMrcJsonMapper;
import com.github.theborakompanioni.openmrc.mother.protobuf.InitialRequestProtobufMother;
import com.google.protobuf.ExtensionRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
        JdbcOpenMrcClientAdapterIT.TestApplictaion.class,
        VishyJdbcITConfig.class
})
public class JdbcOpenMrcClientAdapterIT {
    private final static String TABLE_NAME = "openmrc_request";

    @SpringBootApplication
    public static class TestApplictaion {
        @Bean
        public OpenMrcJsonMapper jsonMapper() {
            return new StandardOpenMrcJsonMapper(
                    ExtensionRegistry.getEmptyRegistry(),
                    new MetricRegistry()
            );
        }

        @Bean
        public OpenMrcJdbcSaveAction openMrcJdbcSaveAction() {
            final String sql = "insert into " + TABLE_NAME + "(type, json) values (?,?)";

            return (jdbcTemplate1, request) -> {
                final String json = jsonMapper().toJson(request);
                jdbcTemplate1.update(sql, request.getType().name(), json);
            };
        }
    }

    @Autowired
    private OpenMrcJsonMapper jsonMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcOpenMrcClientAdapter sut;

    @Test
    public void itShouldPersistToDatabase() throws Exception {
        final OpenMrc.Request initialRequest = new InitialRequestProtobufMother()
                .standardInitialRequest()
                .build();

        this.sut.accept(initialRequest);

        final String sql = "select * from " + TABLE_NAME;
        final Map<String, Object> stringObjectMap = jdbcTemplate.queryForObject(sql, new ColumnMapRowMapper());
        final String json = stringObjectMap.get("json").toString();

        final OpenMrc.Request fromDb = jsonMapper.toOpenMrcRequest(json)
                .blockingSingle()
                .build();

        assertThat(fromDb, is(equalTo(initialRequest)));
    }

}
