package com.hexagonal.framework.config;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {

    @Bean
    public Tracer tracer() {
        // GlobalOpenTelemetry üzerinden tracer örneği alıyoruz.
        // "RichBank-Framework" kısmını projenin adına göre değiştirebilirsin.
        return GlobalOpenTelemetry.getTracer("RichBank-Framework");
    }
}
