package com.github.theborakompanioni.vishy.metrics;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.github.theborakompanioni.openmrc.OpenMrc;
import com.github.theborakompanioni.openmrc.OpenMrcRequestConsumer;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Slf4j
public class VishyMetricsClientAdapter implements OpenMrcRequestConsumer {
    private static final String METRIC_PREFIX = "vishy";

    private final VisibilityStateMetrics initial;
    private final VisibilityTimeReportMetrics status;
    private final VisibilityTimeReportMetrics summary;

    private final Meter incomingRequests;

    public VishyMetricsClientAdapter(MetricRegistry metricsRegistry) {
        this.initial = new VisibilityStateMetrics(METRIC_PREFIX + ".initial", metricsRegistry);
        this.status = new VisibilityTimeReportMetrics(METRIC_PREFIX + ".status", metricsRegistry);
        this.summary = new VisibilityTimeReportMetrics(METRIC_PREFIX, metricsRegistry);

        this.incomingRequests = metricsRegistry.meter(MetricRegistry.name(METRIC_PREFIX, "requests.incoming"));
    }

    @Override
    public void accept(OpenMrc.Request request) {
        requireNonNull(request);

        this.incomingRequests.mark();

        if (log.isDebugEnabled()) {
            log.debug("received event of type {}", request.getType());
        }

        if (request.hasInitial()) {
            onInitial(request.getInitial());
        }
        if (request.hasStatus()) {
            onStatus(request.getStatus());
        }
        if (request.hasSummary()) {
            onSummary(request.getSummary());
        }
    }

    private void onInitial(OpenMrc.InitialContext context) {
        Optional.ofNullable(context)
                .filter(OpenMrc.InitialContext::hasState)
                .map(OpenMrc.InitialContext::getState)
                .ifPresent(this.initial);
    }

    private void onStatus(OpenMrc.StatusContext context) {
        context.getTestList().stream()
                .filter(OpenMrc.PercentageTimeTest::hasTimeReport)
                .map(OpenMrc.PercentageTimeTest::getTimeReport)
                .forEach(this.status);
    }

    private void onSummary(OpenMrc.SummaryContext context) {
        Optional.ofNullable(context)
                .filter(OpenMrc.SummaryContext::hasReport)
                .map(OpenMrc.SummaryContext::getReport)
                .ifPresent(this.summary);
    }

}
