package com.github.theborakompanioni.vishy.segmentio;

import com.github.segmentio.Analytics;
import com.github.segmentio.Client;
import com.github.segmentio.Options;
import com.github.theborakompanioni.openmrc.OpenMrcRequestConsumer;
import com.github.theborakompanioni.vishy.consumer.RequestToMapFunction;
import com.ning.http.client.AsyncHttpClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.Objects.requireNonNull;

@Slf4j
@Configuration
@EnableConfigurationProperties(SegmentIoProperties.class)
@ConditionalOnProperty("vishy.consumer.segmentio.enabled")
public class SegmentIoConfig {

    @Autowired
    private SegmentIoProperties properties;

    @Bean
    public OpenMrcRequestConsumer analyticsClient() {
        return new SegmentOpenMrcClientAdapter(analytics(), new RequestToMapFunction());
    }

    @Bean
    public Client analytics() {
        String writeKey = requireNonNull(properties.getWriteKey(), "Segment write key not specified.");

        Analytics.initialize(writeKey, options());

        return Analytics.getDefaultClient();
    }

    @Bean
    public Options options() {
        return new Options()
                .setFlushAfter(5_000)
                .setMaxQueueSize(30_000)
                .setHttpConfig(asyncHttpClientConfig());
    }

    @Bean
    public AsyncHttpClientConfig asyncHttpClientConfig() {
        return new AsyncHttpClientConfig.Builder()
                .setMaximumConnectionsTotal(100)
                .setConnectionTimeoutInMs(3_000)
                .setMaxRequestRetry(3)
                .build();
    }
}
