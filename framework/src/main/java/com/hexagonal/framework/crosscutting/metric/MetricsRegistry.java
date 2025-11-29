package com.hexagonal.framework.crosscutting.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Central registry for managing application metrics.
 * Provides methods to create and manage counters, timers, and gauges using Micrometer.
 */
@Component
public class MetricsRegistry {

    private final MeterRegistry meterRegistry;
    private final ConcurrentMap<String, Counter> counters = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Timer> timers = new ConcurrentHashMap<>();

    public MetricsRegistry(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Gets or creates a counter with the given name and tags.
     *
     * @param metricName The name of the counter
     * @param tags       The tags to associate with the counter
     * @return The counter instance
     */
    public Counter getOrCreateCounter(String metricName, Tag... tags) {
        String key = buildKey(metricName, tags);
        return counters.computeIfAbsent(key, k ->
            Counter.builder(metricName)
                   .tags(java.util.Arrays.asList(tags))
                   .register(meterRegistry)
        );
    }

    /**
     * Increments a counter by 1.
     *
     * @param metricName The name of the counter
     * @param tags       The tags to associate with the counter
     */
    public void incrementCounter(String metricName, Tag... tags) {
        getOrCreateCounter(metricName, tags).increment();
    }

    /**
     * Increments a counter by the given amount.
     *
     * @param metricName The name of the counter
     * @param amount     The amount to increment
     * @param tags       The tags to associate with the counter
     */
    public void incrementCounter(String metricName, double amount, Tag... tags) {
        getOrCreateCounter(metricName, tags).increment(amount);
    }

    /**
     * Gets or creates a timer with the given name and tags.
     *
     * @param metricName The name of the timer
     * @param tags       The tags to associate with the timer
     * @return The timer instance
     */
    public Timer getOrCreateTimer(String metricName, Tag... tags) {
        String key = buildKey(metricName, tags);
        return timers.computeIfAbsent(key, k ->
            Timer.builder(metricName)
                 .tags(java.util.Arrays.asList(tags))
                 .register(meterRegistry)
        );
    }

    /**
     * Records a timer value.
     *
     * @param metricName The name of the timer
     * @param runnable   The operation to time
     * @param tags       The tags to associate with the timer
     */
    public void recordTimer(String metricName, Runnable runnable, Tag... tags) {
        getOrCreateTimer(metricName, tags).record(runnable);
    }

    /**
     * Records a timer value in a specific unit.
     *
     * @param metricName The name of the timer
     * @param supplier   The operation to time
     * @param tags       The tags to associate with the timer
     * @return The result of the supplier
     */
    public <T> T recordTimer(String metricName, java.util.function.Supplier<T> supplier, Tag... tags) {
        try {
            return getOrCreateTimer(metricName, tags).recordCallable(supplier::get);
        } catch (Exception e) {
            throw new RuntimeException("Error recording timer", e);
        }
    }

    /**
     * Records a gauge value.
     *
     * @param metricName The name of the gauge
     * @param value      The current value of the gauge
     * @param tags       The tags to associate with the gauge
     */
    public void recordGauge(String metricName, double value, Tag... tags) {
        io.micrometer.core.instrument.Gauge.builder(metricName, () -> value)
                .tags(java.util.Arrays.asList(tags))
                .register(meterRegistry);
    }

    /**
     * Records a gauge value using a supplier.
     *
     * @param metricName The name of the gauge
     * @param supplier   A supplier that returns the current value
     * @param tags       The tags to associate with the gauge
     */
    public void recordGauge(String metricName, java.util.function.DoubleSupplier supplier, Tag... tags) {
        io.micrometer.core.instrument.Gauge.builder(metricName, supplier::getAsDouble)
                .tags(java.util.Arrays.asList(tags))
                .register(meterRegistry);
    }

    /**
     * Gets the underlying MeterRegistry.
     *
     * @return The MeterRegistry instance
     */
    public MeterRegistry getMeterRegistry() {
        return meterRegistry;
    }

    /**
     * Builds a unique key for a metric based on its name and tags.
     *
     * @param metricName The metric name
     * @param tags       The tags
     * @return A unique key
     */
    private String buildKey(String metricName, Tag... tags) {
        StringBuilder sb = new StringBuilder(metricName);
        for (Tag tag : tags) {
            sb.append(":").append(tag.getKey()).append("=").append(tag.getValue());
        }
        return sb.toString();
    }
}

