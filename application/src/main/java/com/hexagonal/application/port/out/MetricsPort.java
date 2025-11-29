package com.hexagonal.application.port.out;

/**
 * Port for metrics collection and reporting to monitoring systems like Prometheus.
 * This port is used to record various application metrics during runtime.
 */
public interface MetricsPort {

    /**
     * Records execution time for a service method.
     *
     * @param serviceName The name of the service
     * @param methodName  The name of the method
     * @param executionTimeMs The execution time in milliseconds
     */
    void recordServiceExecutionTime(String serviceName, String methodName, long executionTimeMs);

    /**
     * Increments a counter for service method invocations.
     *
     * @param serviceName The name of the service
     * @param methodName  The name of the method
     */
    void incrementServiceInvocationCount(String serviceName, String methodName);

    /**
     * Records successful service method execution.
     *
     * @param serviceName The name of the service
     * @param methodName  The name of the method
     */
    void recordServiceSuccess(String serviceName, String methodName);

    /**
     * Records failed service method execution.
     *
     * @param serviceName The name of the service
     * @param methodName  The name of the method
     * @param errorType   The type of error that occurred
     */
    void recordServiceFailure(String serviceName, String methodName, String errorType);

    /**
     * Records a gauge value (e.g., cache size, active connections).
     *
     * @param metricName The name of the gauge metric
     * @param value      The value to record
     */
    void recordGaugeValue(String metricName, double value);

    /**
     * Increments a generic counter with the given metric name and tags.
     *
     * @param metricName The name of the counter
     * @param tags       Key-value pairs for metric tags
     */
    void incrementCounter(String metricName, String... tags);

    /**
     * Records a timer value in milliseconds.
     *
     * @param metricName The name of the timer
     * @param valueMs    The value in milliseconds
     * @param tags       Key-value pairs for metric tags
     */
    void recordTimer(String metricName, long valueMs, String... tags);

    /**
     * Increments a business metric (e.g., successful logins, order confirmations).
     *
     * @param metricName The name of the business metric
     * @param tags       Key-value pairs for metric tags
     */
    void incrementBusinessMetric(String metricName, String... tags);
}

