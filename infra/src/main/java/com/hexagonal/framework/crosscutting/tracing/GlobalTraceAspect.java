package com.hexagonal.framework.crosscutting.tracing;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class GlobalTraceAspect {

    private final Tracer tracer;

    /**
     * Hedef: com.hexagonal paketi ve altındaki her şey.
     * Kısıtlama: Tracing paketinin kendisini (sonsuz döngü olmasın diye) hariç tutuyoruz.
     */
    @Pointcut("execution(* com.hexagonal..*(..)) && !within(com.hexagonal.framework.crosscutting.tracing..*)")
    public void monitorValidPackages() {}

    @Around("monitorValidPackages()")
    public Object traceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        String className = signature.getDeclaringType().getSimpleName();

        // FİLTRELEME: Gürültüyü azaltmak için Getter, Setter, toString, hashCode gibi metodları izleme dışı bırakıyoruz.
        if (isTrivialMethod(methodName)) {
            return joinPoint.proceed();
        }

        String spanName = className + "." + methodName;
        Span span = tracer.spanBuilder(spanName).startSpan();

        try (Scope scope = span.makeCurrent()) {
            return joinPoint.proceed();
        } catch (Throwable t) {
            span.recordException(t);
            span.setStatus(StatusCode.ERROR, t.getMessage());
            throw t;
        } finally {
            span.end();
        }
    }

    // Basit metodları elemek için yardımcı kontrol
    private boolean isTrivialMethod(String name) {
        return name.startsWith("get") ||
                name.startsWith("set") ||
                name.equals("toString") ||
                name.equals("hashCode") ||
                name.equals("equals") ||
                name.equals("canEqual");
    }
}