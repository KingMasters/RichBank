package com.hexagonal.framework.crosscutting.metric;

import com.hexagonal.application.port.out.MetricsPort;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * AOP Aspect for automatic metrics collection from application services.
 * Records execution time, success/failure counts, and other metrics for all service methods.
 */
@Aspect
@Component
public class MetricsCollectorAspect {

    private static final Logger logger = LoggerFactory.getLogger("METRICS_ASPECT");

    private final MetricsPort metricsPort;

    public MetricsCollectorAspect(MetricsPort metricsPort) {
        this.metricsPort = metricsPort;
    }

    /**
     * Pointcut for all methods in classes within the com.hexagonal.application.service package.
     */
    @Pointcut("execution(* com.hexagonal.application.service..*.*(..))")
    public void applicationServiceMethods() {
    }

    /**
     * Around advice that collects metrics for application service method executions.
     *
     * @param joinPoint The join point for the intercepted method
     * @return The result of the method execution
     * @throws Throwable If the method execution throws an exception
     */
    @Around("applicationServiceMethods()")
    public Object collectMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        String serviceName = extractServiceName(joinPoint);
        String methodName = joinPoint.getSignature().getName();

        long startTime = System.currentTimeMillis();

        try {
            // Increment invocation count
            metricsPort.incrementServiceInvocationCount(serviceName, methodName);

            // Execute the actual method
            Object result = joinPoint.proceed();

            // Record success
            metricsPort.recordServiceSuccess(serviceName, methodName);

            return result;
        } catch (Throwable throwable) {
            // Record failure with error type
            String errorType = throwable.getClass().getSimpleName();
            metricsPort.recordServiceFailure(serviceName, methodName, errorType);

            throw throwable;
        } finally {
            // Record execution time
            long executionTimeMs = System.currentTimeMillis() - startTime;
            metricsPort.recordServiceExecutionTime(serviceName, methodName, executionTimeMs);

            logger.trace("Method execution recorded: {}.{} took {} ms", serviceName, methodName, executionTimeMs);
        }
    }

    /**
     * Extracts the service name from the class name.
     * Converts fully qualified class name to a simplified service name.
     *
     * @param joinPoint The join point
     * @return The service name
     */
    private String extractServiceName(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        // Remove 'Service' suffix if present
        if (className.endsWith("Service")) {
            return className.substring(0, className.length() - 7);
        }
        return className;
    }
}

