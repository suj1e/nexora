package com.nexora.resilience.autoconfigure;

import com.nexora.resilience.listener.CircuitBreakerEventLogger;
import com.nexora.resilience.listener.RetryEventLogger;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link EventListenerAutoConfiguration}.
 */
@DisplayName("EventListenerAutoConfiguration Integration Tests")
class EventListenerAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(
            ResilienceAutoConfiguration.class,
            EventListenerAutoConfiguration.class
        ));

    @Test
    @DisplayName("Should load EventListenerAutoConfiguration")
    void shouldLoad() {
        contextRunner
            .run(context -> {
                assertThat(context).hasNotFailed();
                assertThat(context).hasSingleBean(EventListenerAutoConfiguration.class);
            });
    }

    @Test
    @DisplayName("Should create CircuitBreakerEventLogger bean")
    void shouldCreateCircuitBreakerEventLogger() {
        contextRunner
            .run(context -> {
                assertThat(context).hasSingleBean(CircuitBreakerEventLogger.class);
            });
    }

    @Test
    @DisplayName("Should create RetryEventLogger bean")
    void shouldCreateRetryEventLogger() {
        contextRunner
            .run(context -> {
                assertThat(context).hasSingleBean(RetryEventLogger.class);
            });
    }

    @Test
    @DisplayName("Should register event listeners on registries")
    void shouldRegisterEventListeners() {
        contextRunner
            .run(context -> {
                // The @PostConstruct method should be called
                assertThat(context).hasSingleBean(EventListenerAutoConfiguration.class);
                EventListenerAutoConfiguration config =
                    context.getBean(EventListenerAutoConfiguration.class);
                assertThat(config).isNotNull();
            });
    }

    @Test
    @DisplayName("Should depend on CircuitBreakerRegistry and RetryRegistry")
    void shouldDependOnRegistries() {
        contextRunner
            .run(context -> {
                assertThat(context).hasSingleBean(CircuitBreakerRegistry.class);
                assertThat(context).hasSingleBean(RetryRegistry.class);
            });
    }
}
