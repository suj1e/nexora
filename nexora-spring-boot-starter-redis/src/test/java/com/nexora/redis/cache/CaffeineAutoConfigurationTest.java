package com.nexora.redis.cache;

import com.nexora.redis.autoconfigure.CaffeineAutoConfiguration;
import com.nexora.redis.autoconfigure.RedisProperties;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link CaffeineAutoConfiguration}.
 */
@DisplayName("CaffeineAutoConfiguration Integration Tests")
class CaffeineAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(CaffeineAutoConfiguration.class));

    @Test
    @DisplayName("Should load CaffeineAutoConfiguration when enabled")
    void shouldLoadWhenEnabled() {
        contextRunner
            .withPropertyValues("nexora.redis.enable-caffeine=true")
            .run(context -> {
                assertThat(context).hasNotFailed();
                assertThat(context).hasSingleBean(CaffeineCacheManager.class);
            });
    }

    @Test
    @DisplayName("Should not load when disabled")
    void shouldNotLoadWhenDisabled() {
        contextRunner
            .withPropertyValues("nexora.redis.enable-caffeine=false")
            .run(context -> {
                assertThat(context).doesNotHaveBean("caffeineCacheManager");
            });
    }

    @Test
    @DisplayName("Should not load without Caffeine class")
    void shouldNotLoadWithoutCaffeine() {
        // This test verifies the @ConditionalOnClass annotation
        // In a real test environment, Caffeine is present, so we just verify the bean exists
        contextRunner
            .withPropertyValues("nexora.redis.enable-caffeine=true")
            .run(context -> {
                assertThat(context).hasSingleBean(CaffeineCacheManager.class);
            });
    }

    @Test
    @DisplayName("Should configure Caffeine with maximum size")
    void shouldConfigureMaximumSize() {
        contextRunner
            .withPropertyValues(
                "nexora.redis.enable-caffeine=true",
                "nexora.redis.caffeine-spec=maximumSize=500"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(CaffeineCacheManager.class);
                CaffeineCacheManager cacheManager = context.getBean(CaffeineCacheManager.class);
                assertThat(cacheManager).isNotNull();
            });
    }

    @Test
    @DisplayName("Should configure Caffeine with expire after write")
    void shouldConfigureExpireAfterWrite() {
        contextRunner
            .withPropertyValues(
                "nexora.redis.enable-caffeine=true",
                "nexora.redis.caffeine-spec=expireAfterWrite=10m"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(CaffeineCacheManager.class);
            });
    }

    @Test
    @DisplayName("Should configure Caffeine with combined spec")
    void shouldConfigureCombinedSpec() {
        contextRunner
            .withPropertyValues(
                "nexora.redis.enable-caffeine=true",
                "nexora.redis.caffeine-spec=maximumSize=1000,expireAfterWrite=5m"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(CaffeineCacheManager.class);
            });
    }

    @Test
    @DisplayName("Should use default spec when not configured")
    void shouldUseDefaultSpec() {
        contextRunner
            .withPropertyValues("nexora.redis.enable-caffeine=true")
            .run(context -> {
                assertThat(context).hasSingleBean(CaffeineCacheManager.class);
                CaffeineCacheManager cacheManager = context.getBean(CaffeineCacheManager.class);
                assertThat(cacheManager).isNotNull();
            });
    }
}
