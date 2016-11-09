package com.github.theborakompanioni.vishy.elasticsearch;

import com.github.theborakompanioni.openmrc.OpenMrc;
import com.github.theborakompanioni.openmrc.mother.InitialRequests;
import com.github.theborakompanioni.vishy.elasticsearch.repository.RequestDocument;
import com.github.theborakompanioni.vishy.elasticsearch.repository.RequestElasticRepository;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ElasticsearchOpenMrcClientAdapterTest {

    private ElasticsearchOpenMrcClientAdapter adapter;
    private RequestElasticRepository repositorySpy;

    @Before
    public void setup() {
        repositorySpy = spy(RequestElasticRepository.class);
        adapter = new ElasticsearchOpenMrcClientAdapter(repositorySpy);
    }

    @Test
    public void itShouldSaveAValidRequest() {
        OpenMrc.Request request = InitialRequests.protobuf().standardGenericRequest().build();
        when(repositorySpy.save(any(RequestDocument.class))).thenReturn(new RequestDocument());

        adapter.accept(request);

        verify(repositorySpy, only()).save(any(RequestDocument.class));
    }

}
