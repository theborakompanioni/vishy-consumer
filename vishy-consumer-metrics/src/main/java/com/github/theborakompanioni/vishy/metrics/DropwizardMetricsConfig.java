package com.github.theborakompanioni.vishy.metrics;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.github.theborakompanioni.openmrc.OpenMrcRequestConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableConfigurationProperties(DropwizardMetricsProperties.class)
@ConditionalOnProperty(value = "vishy.metrics.enabled", matchIfMissing = true)
public class DropwizardMetricsConfig {

    @Autowired
    private DropwizardMetricsProperties properties;

    @Bean
    public OpenMrcRequestConsumer dropwizardMetricsOpenMrcClientAdapter() {
        return new DropwizardMetricsClientAdapter(metricRegistry());
    }

    @Bean
    public MetricRegistry metricRegistry() {
        return new MetricRegistry();
    }

    @Bean
    @ConditionalOnProperty("vishy.metrics.console")
    public ConsoleReporter consoleReporter() {
        ConsoleReporter reporter = ConsoleReporter.forRegistry(metricRegistry())
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();

        reporter.start(properties.getIntervalInSeconds(), TimeUnit.SECONDS);

        return reporter;
    }
}
