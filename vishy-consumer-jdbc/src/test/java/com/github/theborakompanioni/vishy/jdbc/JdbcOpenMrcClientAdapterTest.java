package com.github.theborakompanioni.vishy.jdbc;

import com.github.theborakompanioni.openmrc.OpenMrc;
import com.github.theborakompanioni.openmrc.mapper.OpenMrcJsonMapper;
import com.github.theborakompanioni.openmrc.mother.protobuf.InitialRequestProtobufMother;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class JdbcOpenMrcClientAdapterTest {

    private JdbcOpenMrcClientAdapter sut;

    private JdbcTemplate jdbcTemplate;
    private OpenMrcJsonMapper jsonMapper;

    @Before
    public void setUp() {
        VishyJdbcProperties properties = mock(VishyJdbcProperties.class);
        doReturn("test_table").when(properties).getTableName();

        this.jdbcTemplate = spy(JdbcTemplate.class);
        doReturn(1).when(jdbcTemplate).update(anyString(), Matchers.<String>anyVararg());

        this.jsonMapper = spy(OpenMrcJsonMapper.class);
        doReturn("{ \"type\": \"TEST\"}").when(jsonMapper).toJson(Matchers.<OpenMrc.Request>any());

        this.sut = spy(new JdbcOpenMrcClientAdapter(
                properties,
                jdbcTemplate,
                jsonMapper
        ));
    }

    @Test
    public void accept() throws Exception {
        final OpenMrc.Request initialRequest = new InitialRequestProtobufMother()
                .standardInitialRequest()
                .build();

        sut.accept(initialRequest);

        verify(sut, times(1)).persist(anyString(), anyString());
        verify(jsonMapper, times(1)).toJson(Matchers.<OpenMrc.Request>any());
        verify(jdbcTemplate, times(1)).update(anyString(), Matchers.<String>anyVararg());
    }

}
