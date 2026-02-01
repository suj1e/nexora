package com.nexora.observability.util;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Metrics utility class for recording Micrometer metrics.
 *
 * <p>Provides convenient methods for recording common metrics:
 * <ul>
 *   <li>Counters - for counting events (e.g., requests, errors)</li>
 *   <li>Timers - for measuring duration of operations</li>
 *   <li>Gauges - for tracking current values (e.g., circuit breaker state)</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>
 * &#64;Autowired
 * private MetricsUtil metricsUtil;
 *
 * // Record a counter
 * metricsUtil.recordCounter("http_requests_total", Tags.of("method", "GET", "status", "200"));
 *
 * // Record a timer
 * metricsUtil.recordTimer("operation_duration", Tags.of("operation", "processOrder"), () -> {
 *     return doSomething();
 * });
 *
 * // Record a gauge
 * metricsUtil.recordGauge("queue_size", Tags.of("queue", "processing"), queue::size);
 * </pre>
 *
 * @author sujie
 */
@Component
@ConditionalOnClass(name = "io.micrometer.core.instrument.MeterRegistry")
@ConditionalOnProperty(prefix = "nexora.observability", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class MetricsUtil {

    private static final Logger log = LoggerFactory.getLogger(MetricsUtil.class);

    private final MeterRegistry meterRegistry;

    /**
     * Record a counter metric.
     *
     * @param name the metric name
     * @param tags the tags to attach to the metric
     */
    public void recordCounter(String name, Tags tags) {
        Counter.builder(name)
                .tags(tags)
                .register(meterRegistry)
                .increment();
    }

    /**
     * Record a counter metric with custom delta.
     *
     * @param name the metric name
     * @param tags the tags to attach to the metric
     * @param amount the amount to increment by
     */
    public void recordCounter(String name, Tags tags, double amount) {
        Counter.builder(name)
                .tags(tags)
                .register(meterRegistry)
                .increment(amount);
    }

    /**
     * Record a timer metric for an operation.
     *
     * @param name the metric name
     * @param tags the tags to attach to the metric
     * @param supplier the operation to time
     * @param <T> the return type
     * @return the result of the operation
     */
    public <T> T recordTimer(String name, Tags tags, Supplier<T> supplier) {
        return Timer.builder(name)
                .tags(tags)
                .register(meterRegistry)
                .record(supplier);
    }

    /**
     * Record a timer metric with explicit duration.
     *
     * @param name the metric name
     * @param tags the tags to attach to the metric
     * @param duration the duration of the operation
     */
    public void recordTimer(String name, Tags tags, Duration duration) {
        Timer.builder(name)
                .tags(tags)
                .register(meterRegistry)
                .record(duration);
    }

    /**
     * Record a gauge metric.
     *
     * @param name the metric name
     * @param tags the tags to attach to the metric
     * @param number the gauge value supplier
     * @param <T> the number type
     */
    public <T extends Number> void recordGauge(String name, Tags tags, Supplier<T> number) {
        Gauge.builder(name, number)
                .tags(tags)
                .register(meterRegistry);
    }

    /**
     * Record a gauge metric with a number value.
     *
     * @param name the metric name
     * @param tags the tags to attach to the metric
     * @param value the gauge value
     */
    public void recordGauge(String name, Tags tags, double value) {
        Gauge.builder(name, () -> value)
                .tags(tags)
                .register(meterRegistry);
    }

    /**
     * Record a gateway route request metric.
     *
     * @param routeId the route ID
     * @param status the status (SUCCESS, FAILURE, etc.)
     */
    public void recordRouteRequest(String routeId, String status) {
        recordCounter("gateway_route_requests_total",
                Tags.of("route", routeId, "status", status));
    }

    /**
     * Record a gateway route duration metric.
     *
     * @param routeId the route ID
     * @param supplier the operation to time
     * @param <T> the return type
     * @return the result of the operation
     */
    public <T> T recordRouteDuration(String routeId, Supplier<T> supplier) {
        return recordTimer("gateway_route_duration_seconds",
                Tags.of("route", routeId), supplier);
    }

    /**
     * Record circuit breaker state.
     *
     * @param circuitBreakerName the circuit breaker name
     * @param state the state (OPEN, HALF_OPEN, CLOSED)
     */
    public void recordCircuitBreakerState(String circuitBreakerName, String state) {
        recordGauge("gateway_circuit_breaker_state",
                Tags.of("circuit", circuitBreakerName),
                "open".equals(state) ? 1 : "half_open".equals(state) ? 2 : 0);
    }

    /**
     * Record an authentication event.
     *
     * @param eventType the event type (LOGIN_SUCCESS, LOGIN_FAILURE, etc.)
     */
    public void recordAuthEvent(String eventType) {
        recordCounter("auth_events_total", Tags.of("event", eventType));
    }

    /**
     * Record an API call metric.
     *
     * @param serviceName the target service name
     * @param method the HTTP method
     * @param statusCode the HTTP status code
     * @param duration the call duration
     */
    public void recordApiCall(String serviceName, String method, int statusCode, Duration duration) {
        recordCounter("api_calls_total",
                Tags.of("service", serviceName, "method", method, "status", String.valueOf(statusCode)));
        recordTimer("api_call_duration_seconds",
                Tags.of("service", serviceName, "method", method), duration);
    }

    /**
     * Get the underlying MeterRegistry.
     *
     * @return the MeterRegistry
     */
    public MeterRegistry getMeterRegistry() {
        return meterRegistry;
    }
}
