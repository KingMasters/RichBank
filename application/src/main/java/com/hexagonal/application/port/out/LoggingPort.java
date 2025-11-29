package com.hexagonal.application.port.out;

/**
 * Logging Port - Application layer için mesaj loglama
 * Request/Response ve hataları loglar
 */
public interface LoggingPort {
    void logRequest(String serviceName, String methodName, Object[] args);
    void logResponse(String serviceName, String methodName, Object result, long executionTimeMs);
    void logError(String serviceName, String methodName, Throwable error);
}

