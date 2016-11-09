package com.github.theborakompanioni.vishy.influxdb;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import metrics_influxdb.HttpInfluxdbProtocol;
import metrics_influxdb.InfluxdbProtocol;
import metrics_influxdb.InfluxdbReporter;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@ConditionalOnBean(MetricRegistry.class)
@ConditionalOnProperty("vishy.influxdb.enabled")
@EnableConfigurationProperties(InfluxdbProperties.class)
public class InfluxdbMetricsConfig {

    @Autowired
    private InfluxdbProperties properties;

    @Bean
    public InfluxDB createClient() {
        String url = "http://" + properties.getHost() + ":" + properties.getPort();
        final InfluxDB client = InfluxDBFactory.connect(url, properties.getUsername(), properties.getPassword());

        if (!Strings.isNullOrEmpty(properties.getDatabase())) {
            log.info("create database {} on conflict (name) do nothing", properties.getDatabase());
            client.createDatabase(properties.getDatabase());
        }

        return client;
    }

    @Bean
    public ScheduledReporter influxdbReporter(MetricRegistry metricRegistry) {
        final InfluxdbProtocol influxdbProtocol = influxdbProtocol();

        ScheduledReporter influxdbReporter = InfluxdbReporter
                .forRegistry(metricRegistry)
                .protocol(influxdbProtocol)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(new EmptyMetricsFilter())
                .skipIdleMetrics(properties.isSkipIdleMetrics())
                .transformer(new MeasurementTransformer("vishy"))
                .build();

        influxdbReporter.start(properties.getIntervalInSeconds(), TimeUnit.SECONDS);

        return influxdbReporter;
    }

    @Bean
    public InfluxdbProtocol influxdbProtocol() {
        log.info("prepare influxdb reporter: {}:{}", properties.getHost(), properties.getPort());

        return new HttpInfluxdbProtocol(
                properties.getHost(),
                Integer.valueOf(properties.getPort()),
                properties.getUsername(),
                properties.getPassword(),
                properties.getDatabase()
        );
    }
}
