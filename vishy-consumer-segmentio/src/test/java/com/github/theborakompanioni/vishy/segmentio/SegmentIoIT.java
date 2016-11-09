package com.github.theborakompanioni.vishy.segmentio;

import com.github.theborakompanioni.openmrc.OpenMrc;
import com.github.theborakompanioni.openmrc.OpenMrcTestConfiguration;
import com.github.theborakompanioni.openmrc.mother.json.InitialRequestJsonMother;
import com.google.common.net.HttpHeaders;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = {
                SegmentIoIT.TestApplictaion.class,
                OpenMrcTestConfiguration.class,
                SegmentITConfiguration.class
        })
public class SegmentIoIT {

    @SpringBootApplication
    public static class TestApplictaion {
    }

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private SegmentIoProperties properties;

    @Autowired
    private SegmentOpenMrcClientAdapter clientAdapter;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void itShouldHaveKeenProperties() throws Exception {
        assertThat("Segment write key not specified.", properties.getWriteKey(), not(isEmptyOrNullString()));
    }

    @Test
    public void itShouldHaveAnSegmentOpenMrcClientAdapter() throws Exception {
        assertThat(clientAdapter, is(notNullValue()));
    }

    @Test
    public void itShouldAcceptValidInitialRequests() throws Exception {
        String requestJson = new InitialRequestJsonMother().standardInitialRequest();
        send(requestJson).andExpect(status().isAccepted());

        ArgumentCaptor<OpenMrc.Request> captor = ArgumentCaptor.forClass(OpenMrc.Request.class);
        verify(clientAdapter, times(1)).accept(captor.capture());

        final OpenMrc.Request request = captor.getValue();
        assertThat(request, is(notNullValue()));
        assertThat(request.getType(), is(OpenMrc.RequestType.INITIAL));
    }

    private ResultActions send(String json) throws Exception {
        return mockMvc.perform(post("/openmrc/consume")
                .header(HttpHeaders.USER_AGENT, "Firefox")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(contentType)
                .content(json));
    }
}
