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

    private final VisibilityStateMetrics initialState;
    private final VisibilityTimeReportMetrics statusTime;
    private final VisibilityStateMetrics statusState;
    private final VisibilityTimeReportMetrics summary;

    private final Meter incomingRequests;

    public VishyMetricsClientAdapter(MetricRegistry metricsRegistry) {
        this.initialState = new VisibilityStateMetrics(METRIC_PREFIX + ".initial", metricsRegistry);

        this.statusTime = new VisibilityTimeReportMetrics(METRIC_PREFIX + ".status", metricsRegistry);
        this.statusState = new VisibilityStateMetrics(METRIC_PREFIX + ".status", metricsRegistry);

        this.summary = new VisibilityTimeReportMetrics(METRIC_PREFIX + ".summary", metricsRegistry);

        this.incomingRequests = metricsRegistry.meter(MetricRegistry.name(METRIC_PREFIX, "request.incoming"));
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
                .ifPresent(this.initialState);
    }

    private void onStatus(OpenMrc.StatusContext context) {
        context.getTestList().stream()
                .filter(OpenMrc.PercentageTimeTest::hasTimeReport)
                .map(OpenMrc.PercentageTimeTest::getTimeReport)
                .forEach(this.statusTime);

        context.getTestList().stream()
                .filter(OpenMrc.PercentageTimeTest::hasMonitorState)
                .map(OpenMrc.PercentageTimeTest::getMonitorState)
                .forEach(this.statusState);
    }

    private void onSummary(OpenMrc.SummaryContext context) {
        Optional.ofNullable(context)
                .filter(OpenMrc.SummaryContext::hasReport)
                .map(OpenMrc.SummaryContext::getReport)
                .ifPresent(this.summary);
    }

}
