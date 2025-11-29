package com.hexagonal.framework.logging;

import com.hexagonal.application.port.out.LoggingPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ApplicationServiceLogger implements LoggingPort {
    private static final Logger logger = LoggerFactory.getLogger("APPLICATION_SERVICE");

    @Override
    public void logRequest(String serviceName, String methodName, Object[] args) {
        logger.info("→ [REQUEST] Service: {} | Method: {} | Args: {}",
                serviceName, methodName, formatArgs(args));
    }

    @Override
    public void logResponse(String serviceName, String methodName, Object result, long executionTimeMs) {
        String resultStr = result != null ? result.getClass().getSimpleName() : "void";
        logger.info("← [RESPONSE] Service: {} | Method: {} | Result: {} | ExecutionTime: {}ms",
                serviceName, methodName, resultStr, executionTimeMs);
    }

    @Override
    public void logError(String serviceName, String methodName, Throwable error) {
        logger.error("✗ [ERROR] Service: {} | Method: {} | Error: {}",
                serviceName, methodName, error.getMessage(), error);
    }

    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) return "[]";
        return Arrays.stream(args)
                .map(arg -> arg == null ? "null" : arg.getClass().getSimpleName())
                .toList()
                .toString();
    }
}

