package com.github.theborakompanioni.vishy.metrics;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.github.theborakompanioni.openmrc.OpenMrc;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

class VisibilityStateMetrics implements Consumer<OpenMrc.VisibilityState> {
    private final Histogram percentage;
    private final Histogram visible;
    private final Histogram fullyVisible;
    private final Histogram hidden;

    VisibilityStateMetrics(String prefix, MetricRegistry metricsRegistry) {
        requireNonNull(prefix);
        requireNonNull(metricsRegistry);

        this.percentage = metricsRegistry.histogram(MetricRegistry.name(prefix, "percentage"));
        this.visible = metricsRegistry.histogram(MetricRegistry.name(prefix, "visible"));
        this.fullyVisible = metricsRegistry.histogram(MetricRegistry.name(prefix, "fullyVisible"));
        this.hidden = metricsRegistry.histogram(MetricRegistry.name(prefix, "hidden"));
    }

    @Override
    public void accept(OpenMrc.VisibilityState state) {
        this.percentage.update((int) (state.getPercentage() * 100));
        this.visible.update(state.getVisible() ? 1 : 0);
        this.hidden.update(state.getHidden() ? 1 : 0);
        this.fullyVisible.update(state.getFullyvisible() ? 1 : 0);
    }
}
