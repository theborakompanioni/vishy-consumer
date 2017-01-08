package com.github.theborakompanioni.vishy.metrics;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.github.theborakompanioni.openmrc.OpenMrcRequestConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@ConditionalOnProperty(value = "vishy.consumer.metrics.enabled", matchIfMissing = true)
public class VishyMetricsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MetricRegistry.class)
    public MetricRegistry metricsRegistry() {
        return new MetricRegistry();
    }

    @Configuration
    @EnableConfigurationProperties(VishyMetricsProperties.class)
    public class VishyMetricsConfiguration {

        private VishyMetricsProperties properties;

        public VishyMetricsConfiguration(VishyMetricsProperties properties) {
            this.properties = properties;
        }

        @Bean
        public OpenMrcRequestConsumer metricsOpenMrcClientAdapter() {
            return new VishyMetricsClientAdapter(metricsRegistry());
        }

        @Bean
        @ConditionalOnProperty("vishy.consumer.metrics.console")
        public ConsoleReporter consoleReporter() {
            ConsoleReporter reporter = ConsoleReporter.forRegistry(metricsRegistry())
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .build();

            reporter.start(properties.getIntervalInSeconds(), TimeUnit.SECONDS);

            return reporter;
        }
    }
}
