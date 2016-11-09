package com.github.theborakompanioni.vishy.metrics;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.github.theborakompanioni.openmrc.OpenMrc;
import com.github.theborakompanioni.openmrc.OpenMrcRequestConsumer;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.requireNonNull;

@Slf4j
public class DropwizardMetricsClientAdapter implements OpenMrcRequestConsumer {
    private static final String METRIC_PREFIX = "vishy";

    private class VishyMetrics {
        private final Meter incomingRequests;
        private final Histogram timeVisible;
        private final Histogram timeFullyVisible;
        private final Histogram timeHidden;
        private final Histogram timeTotal;

        public VishyMetrics(String prefix, MetricRegistry metricsRegistry) {
            requireNonNull(metricsRegistry);

            this.incomingRequests = metricsRegistry.meter(MetricRegistry.name(prefix, "requests.incoming"));

            this.timeVisible = metricsRegistry.histogram(MetricRegistry.name(prefix, "time.visible"));
            this.timeFullyVisible = metricsRegistry.histogram(MetricRegistry.name(prefix, "time.fullyvisible"));
            this.timeHidden = metricsRegistry.histogram(MetricRegistry.name(prefix, "time.hidden"));
            this.timeTotal = metricsRegistry.histogram(MetricRegistry.name(prefix, "time.total"));
        }
    }

    private final VishyMetrics vishyMetrics;

    public DropwizardMetricsClientAdapter(MetricRegistry metricsRegistry) {
        this.vishyMetrics = new VishyMetrics(METRIC_PREFIX, metricsRegistry);
    }

    @Override
    public void accept(OpenMrc.Request request) {
        this.vishyMetrics.incomingRequests.mark();

        log.info("received event for metrics {}", request.getType());

        if (request.getType() == OpenMrc.RequestType.SUMMARY) {
            onSummaryRequest(request);
        }

    }

    private void onSummaryRequest(OpenMrc.Request request) {
        OpenMrc.VisibilityTimeReport report = request.getSummary().getReport();
        this.vishyMetrics.timeVisible.update(report.getTimeVisible());
        this.vishyMetrics.timeHidden.update(report.getTimeHidden());
        this.vishyMetrics.timeFullyVisible.update(report.getTimeFullyVisible());
        this.vishyMetrics.timeTotal.update(report.getDuration());
    }

}
