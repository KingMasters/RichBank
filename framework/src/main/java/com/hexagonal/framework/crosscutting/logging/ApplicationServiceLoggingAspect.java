package com.hexagonal.framework.crosscutting.logging;

import com.hexagonal.application.port.out.LoggingPort;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * AOP Aspect - @UseCase ile işaretlenmiş servisler otomatik olarak loglanır
 * Request/Response mesajları ve yürütme zamanı kaydedilir
 */
@Aspect
@Component
public class ApplicationServiceLoggingAspect {
    private final LoggingPort loggingPort;

    public ApplicationServiceLoggingAspect(LoggingPort loggingPort) {
        this.loggingPort = loggingPort;
    }

    @Around("@within(com.hexagonal.application.common.UseCase) && execution(public * execute(..))")
    public Object logApplicationService(ProceedingJoinPoint joinPoint) throws Throwable {
        String serviceName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        long startTime = System.currentTimeMillis();

        loggingPort.logRequest(serviceName, methodName, args);

        try {
            Object result = joinPoint.proceed();
            long executionTimeMs = System.currentTimeMillis() - startTime;
            loggingPort.logResponse(serviceName, methodName, result, executionTimeMs);
            return result;
        } catch (Throwable error) {
            loggingPort.logError(serviceName, methodName, error);
            throw error;
        }
    }
}

