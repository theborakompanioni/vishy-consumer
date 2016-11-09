package com.github.theborakompanioni.vishy.elasticsearch;

import com.github.theborakompanioni.openmrc.OpenMrc;
import com.github.theborakompanioni.openmrc.OpenMrcRequestConsumer;
import com.github.theborakompanioni.vishy.consumer.OpenMrcRequestToMapFunction;
import com.github.theborakompanioni.vishy.consumer.RequestToMapFunction;
import com.github.theborakompanioni.vishy.elasticsearch.repository.RequestDocument;
import com.github.theborakompanioni.vishy.elasticsearch.repository.RequestElasticRepository;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.requireNonNull;

@Slf4j
public class ElasticsearchOpenMrcClientAdapter implements OpenMrcRequestConsumer {

    private RequestElasticRepository template;
    private OpenMrcRequestToMapFunction requestToMapFunction;

    public ElasticsearchOpenMrcClientAdapter(RequestElasticRepository template) {
        this.template = requireNonNull(template);
        requestToMapFunction = new RequestToMapFunction();
    }

    @Override
    public void accept(OpenMrc.Request request) {
        RequestDocument document = new RequestDocument();
        document.setRequest(requestToMapFunction.apply(request));

        final RequestDocument savedDocument = template.save(document);

        if (log.isDebugEnabled()) {
            log.debug("Request saved {}: {}", savedDocument.getId(), savedDocument.getRequest());
        }
    }
}
