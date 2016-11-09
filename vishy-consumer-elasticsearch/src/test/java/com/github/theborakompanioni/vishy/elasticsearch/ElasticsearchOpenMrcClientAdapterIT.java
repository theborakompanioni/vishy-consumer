package com.github.theborakompanioni.vishy.elasticsearch;

import com.github.theborakompanioni.openmrc.OpenMrc;
import com.github.theborakompanioni.openmrc.mother.InitialRequests;
import com.github.theborakompanioni.vishy.elasticsearch.repository.RequestDocument;
import com.github.theborakompanioni.vishy.elasticsearch.repository.RequestElasticRepository;
import com.google.common.collect.Iterables;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = {TestApplictaion.class, VishyElasticsearchConfig.class},
        properties = {
                "vishy.elasticsearch.enabled:true"
        })
public class ElasticsearchOpenMrcClientAdapterIT {

    @Autowired
    private ElasticsearchOpenMrcClientAdapter adapter;

    @Autowired
    private RequestElasticRepository repository;

    @Before
    public void setup() {
        repository.deleteAll();
    }

    @After
    public void teardown() {
        repository.deleteAll();
    }

    @Test
    public void itShouldSaveAValidRequest() {
        assertThat(Iterables.size(repository.findAll()), is(0));

        OpenMrc.Request request = InitialRequests.protobuf().standardGenericRequest().build();

        adapter.accept(request);

        final Iterable<RequestDocument> all = repository.findAll();

        assertThat(Iterables.size(all), is(1));
    }

}
