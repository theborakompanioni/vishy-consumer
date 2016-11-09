package com.github.theborakompanioni.vishy.influxdb;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;

public class EmptyMetricsFilter implements MetricFilter {
    @Override
    public boolean matches(final String name, final Metric metric) {
        if (metric instanceof Gauge) {
            final Object val = ((Gauge) metric).getValue();
            if (val instanceof Double) {
                final Double val1 = (Double) val;
                return isValidDouble(val1) && val1 > 0d;
            } else if (val instanceof Number) {
                return ((Number) val).longValue() > 0d;
            }
        }
        return true;
    }

    private boolean isValidDouble(final Double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value);
    }
}
