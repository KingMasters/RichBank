package com.hexagonal.framework.crosscutting.metric;

import com.hexagonal.application.port.out.MetricsPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("MetricsService Tests")
class MetricsServiceTest {

    private MetricsService metricsService;

    @Mock
    private MetricsPort metricsPort;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        metricsService = new MetricsService(metricsPort);
    }

    @Test
    @DisplayName("Should record execution time")
    void testRecordExecutionTime() {
        // Given
        String serviceName = "UserService";
        String methodName = "findById";
        long executionTimeMs = 150;

        // When
        metricsService.recordExecutionTime(serviceName, methodName, executionTimeMs);

        // Then
        verify(metricsPort).recordServiceExecutionTime(serviceName, methodName, executionTimeMs);
    }

    @Test
    @DisplayName("Should record business event")
    void testRecordBusinessEvent() {
        // Given
        String metricName = "order.created";
        String[] tags = {"status", "confirmed"};

        // When
        metricsService.recordBusinessEvent(metricName, tags);

        // Then
        verify(metricsPort).incrementBusinessMetric(metricName, tags);
    }

    @Test
    @DisplayName("Should record cache operation")
    void testRecordCacheOperation() {
        // Given
        String cacheType = "category_cache";
        String operation = "hit";

        // When
        metricsService.recordCacheOperation(cacheType, operation);

        // Then
        verify(metricsPort).incrementCounter("cache.category_cache.hit");
    }

    @Test
    @DisplayName("Should record API call")
    void testRecordApiCall() {
        // Given
        String endpoint = "/api/login";
        String method = "POST";
        int statusCode = 200;

        // When
        metricsService.recordApiCall(endpoint, method, statusCode);

        // Then
        verify(metricsPort).incrementCounter("api.calls", "endpoint", endpoint, "method", method, "status", "200");
    }

    @Test
    @DisplayName("Should record database operation")
    void testRecordDatabaseOperation() {
        // Given
        String operation = "insert";
        String entity = "Order";
        long executionTimeMs = 50;

        // When
        metricsService.recordDatabaseOperation(operation, entity, executionTimeMs);

        // Then
        verify(metricsPort).recordTimer("database.insert", executionTimeMs, "entity", entity);
    }

    @Test
    @DisplayName("Should record queue operation")
    void testRecordQueueOperation() {
        // Given
        String queueName = "payment_queue";
        String operation = "publish";

        // When
        metricsService.recordQueueOperation(queueName, operation);

        // Then
        verify(metricsPort).incrementCounter("queue.payment_queue.publish");
    }

    @Test
    @DisplayName("Should record error")
    void testRecordError() {
        // Given
        String errorType = "PaymentFailedException";
        String serviceName = "PaymentService";

        // When
        metricsService.recordError(errorType, serviceName);

        // Then
        verify(metricsPort).incrementCounter("errors", "type", errorType, "service", serviceName);
    }

    @Test
    @DisplayName("Should record gauge")
    void testRecordGauge() {
        // Given
        String metricName = "active.connections";
        double value = 150.0;

        // When
        metricsService.recordGauge(metricName, value);

        // Then
        verify(metricsPort).recordGaugeValue(metricName, value);
    }

    @Test
    @DisplayName("Should execute operation with timing on success")
    void testExecuteWithTimingSuccess() throws Exception {
        // Given
        String serviceName = "TestService";
        String methodName = "testMethod";
        MetricsService.TimedOperation operation = () -> {
            // Simulates successful operation
        };

        // When
        metricsService.executeWithTiming(serviceName, methodName, operation);

        // Then
        verify(metricsPort).recordServiceSuccess(serviceName, methodName);
        verify(metricsPort).recordServiceExecutionTime(eq(serviceName), eq(methodName), anyLong());
    }

    @Test
    @DisplayName("Should execute operation with timing on failure")
    void testExecuteWithTimingFailure() throws Exception {
        // Given
        String serviceName = "TestService";
        String methodName = "testMethod";
        Exception testException = new RuntimeException("Test error");
        MetricsService.TimedOperation operation = () -> {
            throw testException;
        };

        // When & Then
        assertThrows(RuntimeException.class, () -> metricsService.executeWithTiming(serviceName, methodName, operation));

        verify(metricsPort).recordServiceFailure(eq(serviceName), eq(methodName), eq("RuntimeException"));
        verify(metricsPort).recordServiceExecutionTime(eq(serviceName), eq(methodName), anyLong());
    }

    @Test
    @DisplayName("Should not record success when operation throws exception")
    void testExecuteWithTimingNoSuccessOnException() throws Exception {
        // Given
        String serviceName = "TestService";
        String methodName = "testMethod";
        MetricsService.TimedOperation operation = () -> {
            throw new IllegalArgumentException("Invalid argument");
        };

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            metricsService.executeWithTiming(serviceName, methodName, operation)
        );

        verify(metricsPort, never()).recordServiceSuccess(anyString(), anyString());
    }
}

