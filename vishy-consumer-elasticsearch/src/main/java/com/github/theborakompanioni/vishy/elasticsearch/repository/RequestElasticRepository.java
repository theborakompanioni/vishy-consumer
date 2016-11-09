package com.github.theborakompanioni.vishy.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

public interface RequestElasticRepository extends ElasticsearchCrudRepository<RequestDocument, String> {
}
