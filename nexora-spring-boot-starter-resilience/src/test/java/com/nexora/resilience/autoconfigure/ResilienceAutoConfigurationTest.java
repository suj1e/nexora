package com.nexora.resilience.autoconfigure;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ResilienceAutoConfiguration}.
 */
@DisplayName("ResilienceAutoConfiguration Integration Tests")
class ResilienceAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(ResilienceAutoConfiguration.class));

    @Test
    @DisplayName("Should load ResilienceAutoConfiguration when enabled")
    void shouldLoadWhenEnabled() {
        contextRunner
            .withPropertyValues("nexora.resilience.enabled=true")
            .run(context -> {
                assertThat(context).hasNotFailed();
                assertThat(context).hasSingleBean(ResilienceAutoConfiguration.class);
            });
    }

    @Test
    @DisplayName("Should load by default (enabled by default)")
    void shouldLoadByDefault() {
        contextRunner
            .run(context -> {
                assertThat(context).hasNotFailed();
                assertThat(context).hasSingleBean(ResilienceAutoConfiguration.class);
            });
    }

    @Test
    @DisplayName("Should not load when disabled")
    void shouldNotLoadWhenDisabled() {
        contextRunner
            .withPropertyValues("nexora.resilience.enabled=false")
            .run(context -> {
                assertThat(context).doesNotHaveBean("circuitBreakerRegistry");
                assertThat(context).doesNotHaveBean("retryRegistry");
            });
    }

    @Test
    @DisplayName("Should create CircuitBreakerRegistry bean")
    void shouldCreateCircuitBreakerRegistry() {
        contextRunner
            .run(context -> {
                assertThat(context).hasSingleBean(CircuitBreakerRegistry.class);
                CircuitBreakerRegistry registry = context.getBean(CircuitBreakerRegistry.class);
                assertThat(registry).isNotNull();
            });
    }

    @Test
    @DisplayName("Should create RetryRegistry bean")
    void shouldCreateRetryRegistry() {
        contextRunner
            .run(context -> {
                assertThat(context).hasSingleBean(RetryRegistry.class);
                RetryRegistry registry = context.getBean(RetryRegistry.class);
                assertThat(registry).isNotNull();
            });
    }

    @Test
    @DisplayName("Should not create TimeLimiterRegistry by default")
    void shouldNotCreateTimeLimiterRegistryByDefault() {
        contextRunner
            .run(context -> {
                assertThat(context).doesNotHaveBean(TimeLimiterRegistry.class);
            });
    }

    @Test
    @DisplayName("Should create TimeLimiterRegistry when enabled")
    void shouldCreateTimeLimiterRegistryWhenEnabled() {
        contextRunner
            .withPropertyValues("nexora.resilience.time-limiter.enabled=true")
            .run(context -> {
                assertThat(context).hasSingleBean(TimeLimiterRegistry.class);
                TimeLimiterRegistry registry = context.getBean(TimeLimiterRegistry.class);
                assertThat(registry).isNotNull();
            });
    }

    @Test
    @DisplayName("Should register ResilienceProperties bean")
    void shouldRegisterResilienceProperties() {
        contextRunner
            .run(context -> {
                assertThat(context).hasSingleBean(ResilienceProperties.class);
            });
    }

    @Test
    @DisplayName("Should configure circuit breaker with custom properties")
    void shouldConfigureCircuitBreakerWithCustomProperties() {
        contextRunner
            .withPropertyValues(
                "nexora.resilience.circuit-breaker.failure-rate-threshold=75",
                "nexora.resilience.circuit-breaker.sliding-window-size=20"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(CircuitBreakerRegistry.class);
            });
    }

    @Test
    @DisplayName("Should configure retry with exponential backoff")
    void shouldConfigureRetryWithExponentialBackoff() {
        contextRunner
            .withPropertyValues(
                "nexora.resilience.retry.enable-exponential-backoff=true",
                "nexora.resilience.retry.exponential-backoff-multiplier=3.0"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(RetryRegistry.class);
            });
    }
}
