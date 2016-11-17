package com.github.theborakompanioni.vishy.metrics;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.github.theborakompanioni.openmrc.OpenMrc;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

class VisibilityTimeReportMetrics implements Consumer<OpenMrc.VisibilityTimeReport> {
    private final Histogram timeVisible;
    private final Histogram timeFullyVisible;
    private final Histogram timeHidden;
    private final Histogram timeTotal;

    VisibilityTimeReportMetrics(String prefix, MetricRegistry metricsRegistry) {
        requireNonNull(prefix);
        requireNonNull(metricsRegistry);

        this.timeVisible = metricsRegistry.histogram(MetricRegistry.name(prefix, "time.visible"));
        this.timeFullyVisible = metricsRegistry.histogram(MetricRegistry.name(prefix, "time.fullyvisible"));
        this.timeHidden = metricsRegistry.histogram(MetricRegistry.name(prefix, "time.hidden"));
        this.timeTotal = metricsRegistry.histogram(MetricRegistry.name(prefix, "time.total"));
    }

    @Override
    public void accept(OpenMrc.VisibilityTimeReport report) {
        this.timeVisible.update(report.getTimeVisible());
        this.timeHidden.update(report.getTimeHidden());
        this.timeFullyVisible.update(report.getTimeFullyVisible());
        this.timeTotal.update(report.getDuration());
    }
}
