package com.github.theborakompanioni.vishy.influxdb;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import metrics_influxdb.api.measurements.MetricMeasurementTransformer;

import java.util.Map;

@Slf4j
public class MeasurementTransformer implements MetricMeasurementTransformer {
    private final String appName;
    private final RateLimiter errorRl = RateLimiter.create(1d);

    public MeasurementTransformer(String appName) {
        this.appName = appName;
    }

    @Override
    public Map<String, String> tags(final String metricName) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        builder.put("app", appName);

        try {
            int commaPos = metricName.indexOf(',');
            if (commaPos > 0) {
                builder.putAll(Splitter.on(',')
                        .omitEmptyStrings()
                        .trimResults()
                        .withKeyValueSeparator('=')
                        .split(metricName.substring(commaPos + 1, metricName.length())));
            }
        } catch (Exception e) {
            if (errorRl.tryAcquire()) {
                log.error("Could not extract Tags from '{}'", metricName, e);
            }
        }

        return builder.build();
    }

    @Override
    public String measurementName(final String metricName) {
        try {
            int commaPos = metricName.indexOf(',');
            return commaPos > 0 ? metricName.substring(0, commaPos) : metricName;
        } catch (Exception e) {
            if (errorRl.tryAcquire()) {
                log.error("Could not extract Measurement from '{}'", metricName, e);
            }
            return metricName;
        }
    }
}
