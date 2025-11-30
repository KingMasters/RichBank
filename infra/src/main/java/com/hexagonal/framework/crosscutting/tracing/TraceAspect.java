package com.hexagonal.framework.crosscutting.tracing;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class TraceAspect {

    private final Tracer tracer;

    @Around("@annotation(traceAnnotation)")
    public Object traceMethod(ProceedingJoinPoint joinPoint, Trace traceAnnotation) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        String className = signature.getDeclaringType().getSimpleName();

        // Eğer anotasyonda özel isim verilmediyse varsayılan olarak Class.Method ismini kullan
        String spanName = traceAnnotation.name().isEmpty()
                ? className + "." + methodName
                : traceAnnotation.name();

        Span span = tracer.spanBuilder(spanName).startSpan();

        // Span'i aktif hale getir (try-with-resources ile otomatik kapanan Scope)
        try (Scope scope = span.makeCurrent()) {
            return joinPoint.proceed();
        } catch (Throwable t) {
            // Hata durumunda Span'e exception bilgisini kaydet
            span.recordException(t);
            span.setStatus(StatusCode.ERROR, t.getMessage());
            throw t;
        } finally {
            // Her durumda Span'i sonlandır
            span.end();
        }
    }
}