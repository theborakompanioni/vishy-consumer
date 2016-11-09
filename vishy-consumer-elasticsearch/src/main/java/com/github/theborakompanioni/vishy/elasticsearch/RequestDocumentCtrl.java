package com.github.theborakompanioni.vishy.elasticsearch;

import com.github.theborakompanioni.vishy.elasticsearch.repository.RequestDocument;
import com.github.theborakompanioni.vishy.elasticsearch.repository.RequestElasticRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static java.util.Objects.requireNonNull;

@RequestMapping(value = "/elastic/request")
public class RequestDocumentCtrl {

    private RequestElasticRepository repository;

    public RequestDocumentCtrl(RequestElasticRepository repository) {
        this.repository = requireNonNull(repository);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<Iterable<RequestDocument>> all() {
        final Iterable<RequestDocument> all = repository.findAll();
        return ResponseEntity.ok(all);
    }
}
