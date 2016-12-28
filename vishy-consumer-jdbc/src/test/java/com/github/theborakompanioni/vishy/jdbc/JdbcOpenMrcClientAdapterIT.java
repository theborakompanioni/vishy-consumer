package com.github.theborakompanioni.vishy.jdbc;

import com.github.theborakompanioni.openmrc.OpenMrc;
import com.github.theborakompanioni.openmrc.mapper.OpenMrcJsonMapper;
import com.github.theborakompanioni.openmrc.mother.protobuf.InitialRequestProtobufMother;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
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

    @SpringBootApplication
    public static class TestApplictaion {
    }

    @Autowired
    private OpenMrcJsonMapper jsonMapper;

    @Autowired
    private VishyJdbcProperties jdbcProperties;

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

        final String sql = "select * from " + jdbcProperties.getTableName();
        final Map<String, Object> stringObjectMap = jdbcTemplate.queryForObject(sql, new ColumnMapRowMapper());
        final String json = stringObjectMap.get("json").toString();

        final OpenMrc.Request fromDb = jsonMapper.toOpenMrcRequest(json).build();

        assertThat(fromDb, is(equalTo(initialRequest)));
    }

}
