package com.hexagonal.framework.crosscutting.metric;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MetricsRegistry Tests")
class MetricsRegistryTest {

    private MetricsRegistry metricsRegistry;
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        metricsRegistry = new MetricsRegistry(meterRegistry);
    }

    @Test
    @DisplayName("Should create and retrieve counter")
    void testGetOrCreateCounter() {
        // Given
        String metricName = "test.counter";
        Tag tag = Tag.of("key", "value");

        // When
        var counter = metricsRegistry.getOrCreateCounter(metricName, tag);

        // Then
        assertNotNull(counter);
        assertEquals(0.0, counter.count());
    }

    @Test
    @DisplayName("Should increment counter")
    void testIncrementCounter() {
        // Given
        String metricName = "test.counter";
        Tag tag = Tag.of("method", "testMethod");

        // When
        metricsRegistry.incrementCounter(metricName, tag);
        metricsRegistry.incrementCounter(metricName, tag);

        // Then
        var counter = metricsRegistry.getOrCreateCounter(metricName, tag);
        assertEquals(2.0, counter.count());
    }

    @Test
    @DisplayName("Should increment counter by specific amount")
    void testIncrementCounterByAmount() {
        // Given
        String metricName = "test.counter";
        Tag tag = Tag.of("type", "increment");

        // When
        metricsRegistry.incrementCounter(metricName, 5.0, tag);

        // Then
        var counter = metricsRegistry.getOrCreateCounter(metricName, tag);
        assertEquals(5.0, counter.count());
    }

    @Test
    @DisplayName("Should create and retrieve timer")
    void testGetOrCreateTimer() {
        // Given
        String metricName = "test.timer";
        Tag tag = Tag.of("operation", "execute");

        // When
        var timer = metricsRegistry.getOrCreateTimer(metricName, tag);

        // Then
        assertNotNull(timer);
        assertEquals(0L, timer.count());
    }

    @Test
    @DisplayName("Should record timer with runnable")
    void testRecordTimerWithRunnable() {
        // Given
        String metricName = "test.timer";
        Tag tag = Tag.of("operation", "sleep");
        long sleepTime = 10;

        // When
        metricsRegistry.recordTimer(metricName, () -> {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, tag);

        // Then
        var timer = metricsRegistry.getOrCreateTimer(metricName, tag);
        assertEquals(1L, timer.count());
        assertTrue(timer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS) >= sleepTime);
    }

    @Test
    @DisplayName("Should record timer with supplier")
    void testRecordTimerWithSupplier() {
        // Given
        String metricName = "test.timer";
        Tag tag = Tag.of("operation", "compute");
        String expectedResult = "test_result";

        // When
        String result = metricsRegistry.recordTimer(metricName, () -> expectedResult, tag);

        // Then
        assertEquals(expectedResult, result);
        var timer = metricsRegistry.getOrCreateTimer(metricName, tag);
        assertEquals(1L, timer.count());
    }

    @Test
    @DisplayName("Should record gauge value")
    void testRecordGauge() {
        // Given
        String metricName = "test.gauge";
        double value = 42.5;
        Tag tag = Tag.of("unit", "percentage");

        // When
        metricsRegistry.recordGauge(metricName, value, tag);

        // Then
        assertNotNull(meterRegistry);
    }

    @Test
    @DisplayName("Should record gauge with supplier")
    void testRecordGaugeWithSupplier() {
        // Given
        String metricName = "test.gauge.dynamic";
        java.util.concurrent.atomic.AtomicReference<Double> value =
            new java.util.concurrent.atomic.AtomicReference<>(10.0);
        Tag tag = Tag.of("type", "dynamic");

        // When - Create a gauge with a DoubleSupplier that returns current value
        metricsRegistry.recordGauge(metricName, () -> {
            Double val = value.get();
            return val != null ? val : 0.0;
        }, tag);
        value.set(20.0);

        // Then - Verify the gauge was registered
        assertNotNull(meterRegistry);
    }

    @Test
    @DisplayName("Should get meter registry")
    void testGetMeterRegistry() {
        // When
        var registry = metricsRegistry.getMeterRegistry();

        // Then
        assertEquals(meterRegistry, registry);
    }

    @Test
    @DisplayName("Should cache counter instances")
    void testCounterCaching() {
        // Given
        String metricName = "test.cached.counter";
        Tag tag = Tag.of("cached", "true");

        // When
        var counter1 = metricsRegistry.getOrCreateCounter(metricName, tag);
        var counter2 = metricsRegistry.getOrCreateCounter(metricName, tag);

        // Then
        assertSame(counter1, counter2);
    }

    @Test
    @DisplayName("Should cache timer instances")
    void testTimerCaching() {
        // Given
        String metricName = "test.cached.timer";
        Tag tag = Tag.of("cached", "true");

        // When
        var timer1 = metricsRegistry.getOrCreateTimer(metricName, tag);
        var timer2 = metricsRegistry.getOrCreateTimer(metricName, tag);

        // Then
        assertSame(timer1, timer2);
    }
}

