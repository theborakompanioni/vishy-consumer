package com.github.theborakompanioni.vishy.metrics;

import com.github.theborakompanioni.openmrc.OpenMrcRequestConsumer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.spy;

@Configuration
@EnableConfigurationProperties(DropwizardMetricsProperties.class)
@ConditionalOnProperty(value = "vishy.metrics.enabled", matchIfMissing = true)
class MetricsITConfiguration extends DropwizardMetricsConfig {

    @Override
    @Bean
    @Primary
    public OpenMrcRequestConsumer dropwizardMetricsOpenMrcClientAdapter() {
        return spy(super.dropwizardMetricsOpenMrcClientAdapter());
    }
}
