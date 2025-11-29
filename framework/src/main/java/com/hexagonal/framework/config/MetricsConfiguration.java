package com.hexagonal.framework.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Micrometer metrics and Prometheus monitoring.
 * Enables metrics collection and exposes them via the /actuator/prometheus endpoint.
 */
@Configuration
public class MetricsConfiguration {

    /**
     * Customizes the Meter Registry with application-specific tags.
     *
     * @return A MeterRegistryCustomizer that adds common tags to all metrics
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
                .commonTags(
                        "application", "richbank",
                        "environment", getEnvironment(),
                        "version", getApplicationVersion()
                );
    }

    /**
     * Gets the current environment (development, staging, production).
     * This can be configured via application properties.
     *
     * @return The environment name
     */
    private String getEnvironment() {
        return System.getProperty("app.environment", "development");
    }

    /**
     * Gets the application version.
     * This can be configured via application properties.
     *
     * @return The application version
     */
    private String getApplicationVersion() {
        return System.getProperty("app.version", "0.0.1-SNAPSHOT");
    }
}

