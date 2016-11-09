package com.github.theborakompanioni.vishy.elasticsearch;

import com.github.theborakompanioni.openmrc.OpenMrcRequestConsumer;
import com.github.theborakompanioni.vishy.elasticsearch.repository.RequestElasticRepository;
import com.github.theborakompanioni.vishy.elasticsearch.repository._package;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Slf4j
@Configuration
@EnableConfigurationProperties(VishyElasticsearchProperties.class)
@ConditionalOnProperty("vishy.elasticsearch.enabled")
@EnableElasticsearchRepositories(basePackageClasses = _package.class)
public class VishyElasticsearchConfig {

    @Autowired
    private Environment environment;

    @Autowired
    private VishyElasticsearchProperties properties;

    @Bean
    public OpenMrcRequestConsumer elasticsearchOpenMrcClientAdapter(RequestElasticRepository template) {
        return new ElasticsearchOpenMrcClientAdapter(template);
    }

    @Bean
    public RequestDocumentCtrl requestDocumentCtrl(RequestElasticRepository template) {
        return new RequestDocumentCtrl(template);
    }
}
