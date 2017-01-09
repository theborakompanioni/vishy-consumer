package com.github.theborakompanioni.vishy.jdbc;

import com.github.theborakompanioni.openmrc.OpenMrc;
import com.github.theborakompanioni.openmrc.mother.protobuf.InitialRequestProtobufMother;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class JdbcOpenMrcClientAdapterTest {

    private JdbcOpenMrcClientAdapter sut;

    private JdbcTemplate jdbcTemplate;
    private OpenMrcJdbcSaveAction saveAction;

    @Before
    public void setUp() {
        this.jdbcTemplate = spy(JdbcTemplate.class);
        this.saveAction = spy(OpenMrcJdbcSaveAction.class);

        this.sut = spy(new JdbcOpenMrcClientAdapter(jdbcTemplate, saveAction));
    }

    @Test
    public void accept() throws Exception {
        final OpenMrc.Request initialRequest = new InitialRequestProtobufMother()
                .standardInitialRequest()
                .build();

        sut.accept(initialRequest);

        verify(saveAction, times(1)).apply(argThat(is(jdbcTemplate)), argThat(is(initialRequest)));
    }

}
