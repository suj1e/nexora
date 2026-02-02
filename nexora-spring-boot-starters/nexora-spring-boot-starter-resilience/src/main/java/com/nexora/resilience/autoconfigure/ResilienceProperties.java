package com.nexora.resilience.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Resilience4j properties.
 *
 * <p>Configuration example:
 * <pre>
 * nexora.resilience.circuit-breaker.enabled=true
 * nexora.resilience.circuit-breaker.failure-rate-threshold=50
 * nexora.resilience.circuit-breaker.wait-duration-in-open-state=10s
 * nexora.resilience.retry.max-attempts=3
 * nexora.resilience.retry.wait-duration=1s
 * </pre>
 *
 * @author sujie
 */
@Data
@ConfigurationProperties(prefix = "nexora.resilience")
public class ResilienceProperties {

    /**
     * Circuit breaker configuration.
     */
    private CircuitBreaker circuitBreaker = new CircuitBreaker();

    /**
     * Retry configuration.
     */
    private Retry retry = new Retry();

    /**
     * Time limiter configuration.
     */
    private TimeLimiter timeLimiter = new TimeLimiter();

    /**
     * Rate limiter configuration.
     */
    private RateLimiter rateLimiter = new RateLimiter();

    @Data
    public static class CircuitBreaker {
        /**
         * Enable circuit breaker.
         */
        private boolean enabled = true;

        /**
         * Failure rate threshold in percentage.
         */
        private float failureRateThreshold = 50f;

        /**
         * Wait duration in open state.
         */
        private Duration waitDurationInOpenState = Duration.ofSeconds(10);

        /**
         * Permitted number of calls in half-open state.
         */
        private int permittedNumberOfCallsInHalfOpenState = 3;

        /**
         * Sliding window size.
         */
        private int slidingWindowSize = 10;

        /**
         * Minimum number of calls required before circuit breaker can calculate error rate.
         */
        private int minimumNumberOfCalls = 5;

        /**
         * Per-instance circuit breaker configurations.
         * Allows overriding default settings for specific instances.
         *
         * <p>Configuration example:
         * <pre>
         * nexora.resilience.circuit-breaker.instances.user-service.failure-rate-threshold=60
         * nexora.resilience.circuit-breaker.instances.user-service.wait-duration-in-open-state=15s
         * nexora.resilience.circuit-breaker.instances.order-service.sliding-window-size=20
         * </pre>
         */
        private Map<String, CircuitBreakerInstanceConfig> instances = new HashMap<>();
    }

    @Data
    public static class CircuitBreakerInstanceConfig {
        /**
         * Failure rate threshold in percentage.
         */
        private Float failureRateThreshold;

        /**
         * Wait duration in open state.
         */
        private Duration waitDurationInOpenState;

        /**
         * Permitted number of calls in half-open state.
         */
        private Integer permittedNumberOfCallsInHalfOpenState;

        /**
         * Sliding window size.
         */
        private Integer slidingWindowSize;

        /**
         * Minimum number of calls required before circuit breaker can calculate error rate.
         */
        private Integer minimumNumberOfCalls;

        /**
         * Sliding window type (count-based or time-based).
         */
        private String slidingWindowType;

        /**
         * Slow call duration threshold.
         */
        private Duration slowCallDurationThreshold;

        /**
         * Slow call rate threshold.
         */
        private Float slowCallRateThreshold;
    }

    @Data
    public static class Retry {
        /**
         * Enable retry.
         */
        private boolean enabled = true;

        /**
         * Maximum number of retry attempts.
         */
        private int maxAttempts = 3;

        /**
         * Wait duration between retry attempts.
         */
        private Duration waitDuration = Duration.ofSeconds(1);

        /**
         * Enable exponential backoff.
         */
        private boolean enableExponentialBackoff = false;

        /**
         * Exponential backoff multiplier.
         */
        private double exponentialBackoffMultiplier = 2.0;
    }

    @Data
    public static class TimeLimiter {
        /**
         * Enable time limiter.
         */
        private boolean enabled = false;

        /**
         * Default timeout duration.
         */
        private Duration timeoutDuration = Duration.ofSeconds(5);
    }

    @Data
    public static class RateLimiter {
        /**
         * Enable rate limiter.
         */
        private boolean enabled = false;

        /**
         * Limit for period.
         */
        private int limitForPeriod = 10;

        /**
         * Limit refresh period.
         */
        private Duration limitRefreshPeriod = Duration.ofSeconds(1);

        /**
         * Timeout duration.
         */
        private Duration timeoutDuration = Duration.ofSeconds(5);
    }
}
