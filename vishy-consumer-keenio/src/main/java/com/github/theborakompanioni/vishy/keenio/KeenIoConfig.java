package com.github.theborakompanioni.vishy.keenio;

import com.github.theborakompanioni.openmrc.OpenMrcRequestConsumer;
import com.github.theborakompanioni.vishy.consumer.RequestToMapFunction;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import io.keen.client.java.GlobalPropertiesEvaluator;
import io.keen.client.java.JavaKeenClientBuilder;
import io.keen.client.java.KeenClient;
import io.keen.client.java.KeenProject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Map;

import static java.util.Objects.requireNonNull;

@Slf4j
@Configuration
@EnableConfigurationProperties(KeenIoProperties.class)
@ConditionalOnProperty("vishy.keenio.enabled")
public class KeenIoConfig {

    @Autowired
    private Environment environment;

    @Autowired
    private KeenIoProperties properties;

    @Bean
    public OpenMrcRequestConsumer keenOpenMrcClientAdapter() {
        return new KeenOpenMrcClientAdapter(keenClient(), new RequestToMapFunction());
    }

    @Bean
    public KeenClient keenClient() {
        KeenClient client = new JavaKeenClientBuilder().build();

        client.setDefaultProject(keenProject());

        client.setGlobalProperties(globalProperties());
        client.setGlobalPropertiesEvaluator(globalPropertiesEvaluator());

        client.setDebugMode(true);

        return client;
    }

    @Bean
    public GlobalPropertiesEvaluator globalPropertiesEvaluator() {
        return s -> {
            Map<String, Object> map = Maps.newHashMap();
            map.put("_vishy_container_id", "docker" + (Math.random() * 10) % 10);
            return map;
        };
    }

    @Bean
    public KeenProject keenProject() {
        String projectId = requireNonNull(properties.getProjectId(), "Keen project id not specified.");
        String writeKey = requireNonNull(properties.getWriteKey(), "Keen write key not specified.");
        String readKey = requireNonNull(properties.getReadKey(), "Keen read key not specified.");

        log.debug("Keen project id: {}", projectId);
        log.debug("Keen write key: {}", writeKey);
        log.debug("Keen read key: {}", readKey);

        return new KeenProject(projectId, writeKey, readKey);
    }

    public Map<String, Object> globalProperties() {
        return ImmutableMap.<String, Object>builder()
                .put("_vishy_server_id", "kepler273")
                .build();
    }
}
