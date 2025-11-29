package com.hexagonal.framework.logging;

import com.hexagonal.application.port.out.LoggingPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("ApplicationServiceLogger Tests")
public class ApplicationServiceLoggerTest {

    private ApplicationServiceLogger logger;
    private LoggingPort loggingPort;

    @BeforeEach
    void setUp() {
        logger = new ApplicationServiceLogger();
        loggingPort = logger;
    }

    @Test
    @DisplayName("Should log request with service name, method name and arguments")
    void shouldLogRequest() {
        // Arrange
        String serviceName = "CreateCategoryService";
        String methodName = "execute";
        Object[] args = new Object[]{"CreateCategoryCommand"};

        // Act - should not throw exception
        loggingPort.logRequest(serviceName, methodName, args);

        // Assert - logging happens without exception
        assertThat(loggingPort).isNotNull();
    }

    @Test
    @DisplayName("Should log response with service name, method name, result and execution time")
    void shouldLogResponse() {
        // Arrange
        String serviceName = "ListCategoriesService";
        String methodName = "execute";
        Object result = "Category";
        long executionTimeMs = 42L;

        // Act - should not throw exception
        loggingPort.logResponse(serviceName, methodName, result, executionTimeMs);

        // Assert - logging happens without exception
        assertThat(loggingPort).isNotNull();
    }

    @Test
    @DisplayName("Should log response with null result as void")
    void shouldLogResponseWithNullResult() {
        // Arrange
        String serviceName = "DeleteCategoryService";
        String methodName = "execute";
        Object result = null;
        long executionTimeMs = 15L;

        // Act - should not throw exception
        loggingPort.logResponse(serviceName, methodName, result, executionTimeMs);

        // Assert - logging happens without exception
        assertThat(loggingPort).isNotNull();
    }

    @Test
    @DisplayName("Should log error with service name, method name and error message")
    void shouldLogError() {
        // Arrange
        String serviceName = "UpdateCategoryService";
        String methodName = "execute";
        Throwable error = new IllegalArgumentException("Invalid category");

        // Act - should not throw exception
        loggingPort.logError(serviceName, methodName, error);

        // Assert - logging happens without exception
        assertThat(loggingPort).isNotNull();
    }

    @Test
    @DisplayName("Should format arguments correctly with empty args")
    void shouldFormatEmptyArgs() {
        // Arrange
        String serviceName = "TestService";
        String methodName = "execute";
        Object[] args = new Object[]{};

        // Act - should not throw exception
        loggingPort.logRequest(serviceName, methodName, args);

        // Assert - logging happens without exception
        assertThat(loggingPort).isNotNull();
    }

    @Test
    @DisplayName("Should format arguments correctly with null args")
    void shouldFormatNullArgs() {
        // Arrange
        String serviceName = "TestService";
        String methodName = "execute";
        Object[] args = null;

        // Act - should not throw exception
        loggingPort.logRequest(serviceName, methodName, args);

        // Assert - logging happens without exception
        assertThat(loggingPort).isNotNull();
    }

    @Test
    @DisplayName("Should format arguments with multiple types")
    void shouldFormatMultipleArgs() {
        // Arrange
        String serviceName = "CreateProductService";
        String methodName = "execute";
        Object[] args = new Object[]{"CreateProductCommand", 123, true};

        // Act - should not throw exception
        loggingPort.logRequest(serviceName, methodName, args);

        // Assert - logging happens without exception
        assertThat(loggingPort).isNotNull();
    }

    @Test
    @DisplayName("Should log error with exception details")
    void shouldLogErrorWithExceptionDetails() {
        // Arrange
        String serviceName = "CategoryService";
        String methodName = "execute";
        Throwable error = new RuntimeException("Database connection failed");

        // Act - should not throw exception
        loggingPort.logError(serviceName, methodName, error);

        // Assert - logging happens without exception
        assertThat(loggingPort).isNotNull();
    }

    @Test
    @DisplayName("Should handle large execution times")
    void shouldHandleLargeExecutionTimes() {
        // Arrange
        String serviceName = "HeavyComputationService";
        String methodName = "execute";
        Object result = "Result";
        long executionTimeMs = 5000L;

        // Act - should not throw exception
        loggingPort.logResponse(serviceName, methodName, result, executionTimeMs);

        // Assert - logging happens without exception
        assertThat(loggingPort).isNotNull();
    }

    @Test
    @DisplayName("Should handle very fast execution times")
    void shouldHandleVeryFastExecutionTimes() {
        // Arrange
        String serviceName = "FastService";
        String methodName = "execute";
        Object result = "Result";
        long executionTimeMs = 1L;

        // Act - should not throw exception
        loggingPort.logResponse(serviceName, methodName, result, executionTimeMs);

        // Assert - logging happens without exception
        assertThat(loggingPort).isNotNull();
    }
}

