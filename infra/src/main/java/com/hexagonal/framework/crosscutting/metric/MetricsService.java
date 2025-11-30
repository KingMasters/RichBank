package com.hexagonal.framework.crosscutting.metric;

import com.hexagonal.application.port.out.MetricsPort;
import org.springframework.stereotype.Component;

/**
 * Utility component for easy access to metrics recording functionality.
 * Can be injected into any Spring component for recording custom metrics.
 */
@Component
public class MetricsService {

    private final MetricsPort metricsPort;

    public MetricsService(MetricsPort metricsPort) {
        this.metricsPort = metricsPort;
    }

    /**
     * Records execution time for a service method.
     *
     * @param serviceName The name of the service
     * @param methodName  The name of the method
     * @param executionTimeMs The execution time in milliseconds
     */
    public void recordExecutionTime(String serviceName, String methodName, long executionTimeMs) {
        metricsPort.recordServiceExecutionTime(serviceName, methodName, executionTimeMs);
    }

    /**
     * Records a custom business metric (e.g., successful login, order placed).
     *
     * @param metricName The name of the metric
     * @param tags       Optional key-value tags
     */
    public void recordBusinessEvent(String metricName, String... tags) {
        metricsPort.incrementBusinessMetric(metricName, tags);
    }

    /**
     * Records a cache operation metric.
     *
     * @param cacheType The type of cache (e.g., "category_cache", "user_cache")
     * @param operation The cache operation (e.g., "hit", "miss", "evict")
     */
    public void recordCacheOperation(String cacheType, String operation) {
        metricsPort.incrementCounter("cache." + cacheType + "." + operation);
    }

    /**
     * Records an API endpoint call metric.
     *
     * @param endpoint The API endpoint
     * @param method   The HTTP method
     * @param statusCode The HTTP status code
     */
    public void recordApiCall(String endpoint, String method, int statusCode) {
        metricsPort.incrementCounter("api.calls", "endpoint", endpoint, "method", method, "status", String.valueOf(statusCode));
    }

    /**
     * Records a database operation metric.
     *
     * @param operation The database operation (e.g., "insert", "update", "delete", "select")
     * @param entity    The entity type
     * @param executionTimeMs The execution time in milliseconds
     */
    public void recordDatabaseOperation(String operation, String entity, long executionTimeMs) {
        metricsPort.recordTimer("database." + operation, executionTimeMs, "entity", entity);
    }

    /**
     * Records a queue/message operation metric.
     *
     * @param queueName The name of the queue
     * @param operation The operation (e.g., "publish", "consume")
     */
    public void recordQueueOperation(String queueName, String operation) {
        metricsPort.incrementCounter("queue." + queueName + "." + operation);
    }

    /**
     * Records an error/exception metric.
     *
     * @param errorType The type of error
     * @param serviceName The service where the error occurred
     */
    public void recordError(String errorType, String serviceName) {
        metricsPort.incrementCounter("errors", "type", errorType, "service", serviceName);
    }

    /**
     * Records a gauge value for tracking current state (e.g., active connections, cache size).
     *
     * @param metricName The name of the gauge metric
     * @param value      The current value
     */
    public void recordGauge(String metricName, double value) {
        metricsPort.recordGaugeValue(metricName, value);
    }

    /**
     * Execute a timed operation with automatic metric recording.
     *
     * @param serviceName The service name
     * @param methodName  The method name
     * @param operation   The operation to execute
     * @throws Exception If the operation throws an exception
     */
    public void executeWithTiming(String serviceName, String methodName, TimedOperation operation) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            operation.execute();
            metricsPort.recordServiceSuccess(serviceName, methodName);
        } catch (Exception e) {
            metricsPort.recordServiceFailure(serviceName, methodName, e.getClass().getSimpleName());
            throw e;
        } finally {
            long executionTimeMs = System.currentTimeMillis() - startTime;
            metricsPort.recordServiceExecutionTime(serviceName, methodName, executionTimeMs);
        }
    }

    /**
     * Functional interface for operations that should be timed.
     */
    @FunctionalInterface
    public interface TimedOperation {
        void execute() throws Exception;
    }
}

