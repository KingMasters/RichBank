package com.hexagonal.framework.crosscutting.metric;

import com.hexagonal.application.port.out.MetricsPort;
import com.hexagonal.framework.common.MetricsAdapter;
import io.micrometer.core.instrument.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter that implements MetricsPort for Prometheus metrics collection.
 * Uses Micrometer to record various application metrics that are exposed to Prometheus.
 */
@MetricsAdapter
public class PrometheusMetricsAdapter implements MetricsPort {

    private static final Logger logger = LoggerFactory.getLogger("METRICS");

    private final MetricsRegistry metricsRegistry;

    // Metric names
    private static final String SERVICE_EXECUTION_TIME = "application.service.execution.time";
    private static final String SERVICE_INVOCATION_COUNT = "application.service.invocation.count";
    private static final String SERVICE_SUCCESS_COUNT = "application.service.success.count";
    private static final String SERVICE_FAILURE_COUNT = "application.service.failure.count";
    private static final String BUSINESS_METRIC_PREFIX = "application.business";

    public PrometheusMetricsAdapter(MetricsRegistry metricsRegistry) {

        this.metricsRegistry = metricsRegistry;
    }

    @Override
    public void recordServiceExecutionTime(String serviceName, String methodName, long executionTimeMs) {
        try {
            Tag[] tags = new Tag[]{
                Tag.of("service", serviceName),
                Tag.of("method", methodName)
            };
            metricsRegistry.recordTimer(SERVICE_EXECUTION_TIME, () -> {}, tags);

            // Also record as a gauge for the latest execution time
            String gaugeMetricName = SERVICE_EXECUTION_TIME + ".latest";
            metricsRegistry.recordGauge(gaugeMetricName, executionTimeMs, tags);

            logger.debug("Recorded execution time: {} ms for {}.{}", executionTimeMs, serviceName, methodName);
        } catch (Exception e) {
            logger.warn("Failed to record service execution time", e);
        }
    }

    @Override
    public void incrementServiceInvocationCount(String serviceName, String methodName) {
        try {
            Tag[] tags = new Tag[]{
                Tag.of("service", serviceName),
                Tag.of("method", methodName)
            };
            metricsRegistry.incrementCounter(SERVICE_INVOCATION_COUNT, tags);

            logger.debug("Incremented invocation count for {}.{}", serviceName, methodName);
        } catch (Exception e) {
            logger.warn("Failed to increment service invocation count", e);
        }
    }

    @Override
    public void recordServiceSuccess(String serviceName, String methodName) {
        try {
            Tag[] tags = new Tag[]{
                Tag.of("service", serviceName),
                Tag.of("method", methodName)
            };
            metricsRegistry.incrementCounter(SERVICE_SUCCESS_COUNT, tags);

            logger.debug("Recorded success for {}.{}", serviceName, methodName);
        } catch (Exception e) {
            logger.warn("Failed to record service success", e);
        }
    }

    @Override
    public void recordServiceFailure(String serviceName, String methodName, String errorType) {
        try {
            Tag[] tags = new Tag[]{
                Tag.of("service", serviceName),
                Tag.of("method", methodName),
                Tag.of("error_type", errorType)
            };
            metricsRegistry.incrementCounter(SERVICE_FAILURE_COUNT, tags);

            logger.warn("Recorded failure for {}.{} - Error: {}", serviceName, methodName, errorType);
        } catch (Exception e) {
            logger.warn("Failed to record service failure", e);
        }
    }

    @Override
    public void recordGaugeValue(String metricName, double value) {
        try {
            String fullMetricName = BUSINESS_METRIC_PREFIX + "." + metricName;
            metricsRegistry.recordGauge(fullMetricName, value);

            logger.debug("Recorded gauge value: {} = {}", fullMetricName, value);
        } catch (Exception e) {
            logger.warn("Failed to record gauge value for metric: {}", metricName, e);
        }
    }

    @Override
    public void incrementCounter(String metricName, String... tags) {
        try {
            String fullMetricName = BUSINESS_METRIC_PREFIX + "." + metricName;
            Tag[] tagArray = parseTags(tags);
            metricsRegistry.incrementCounter(fullMetricName, tagArray);

            logger.debug("Incremented counter: {}", fullMetricName);
        } catch (Exception e) {
            logger.warn("Failed to increment counter: {}", metricName, e);
        }
    }

    @Override
    public void recordTimer(String metricName, long valueMs, String... tags) {
        try {
            String fullMetricName = BUSINESS_METRIC_PREFIX + "." + metricName;
            Tag[] tagArray = parseTags(tags);

            // Record using registry's timer recording capability
            metricsRegistry.getOrCreateTimer(fullMetricName, tagArray)
                    .record(valueMs, java.util.concurrent.TimeUnit.MILLISECONDS);

            logger.debug("Recorded timer: {} = {} ms", fullMetricName, valueMs);
        } catch (Exception e) {
            logger.warn("Failed to record timer: {}", metricName, e);
        }
    }

    @Override
    public void incrementBusinessMetric(String metricName, String... tags) {
        try {
            String fullMetricName = BUSINESS_METRIC_PREFIX + "." + metricName;
            Tag[] tagArray = parseTags(tags);
            metricsRegistry.incrementCounter(fullMetricName, tagArray);

            logger.debug("Incremented business metric: {}", fullMetricName);
        } catch (Exception e) {
            logger.warn("Failed to increment business metric: {}", metricName, e);
        }
    }

    /**
     * Parses string array of tags (key1, value1, key2, value2, ...) into Tag array.
     *
     * @param tags Variable length array of tags in key-value pairs
     * @return Array of Tag objects
     */
    private Tag[] parseTags(String... tags) {
        if (tags == null || tags.length == 0) {
            return new Tag[0];
        }

        if (tags.length % 2 != 0) {
            throw new IllegalArgumentException("Tags must be provided in key-value pairs");
        }

        Tag[] tagArray = new Tag[tags.length / 2];
        for (int i = 0; i < tags.length; i += 2) {
            tagArray[i / 2] = Tag.of(tags[i], tags[i + 1]);
        }
        return tagArray;
    }
}

