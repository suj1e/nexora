package com.nexora.redis.cache;

import com.nexora.redis.autoconfigure.RedisProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link RedisCacheAutoConfiguration}.
 */
@DisplayName("RedisCacheAutoConfiguration Integration Tests")
class RedisCacheAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(RedisCacheAutoConfiguration.class))
        .withBean(RedisConnectionFactory.class, () -> new LettuceConnectionFactory());

    @Test
    @DisplayName("Should load RedisCacheAutoConfiguration when enabled")
    void shouldLoadWhenEnabled() {
        contextRunner
            .withPropertyValues("nexora.redis.enabled=true")
            .run(context -> {
                assertThat(context).hasNotFailed();
                assertThat(context).hasSingleBean(CacheManager.class);
            });
    }

    @Test
    @DisplayName("Should not load when disabled")
    void shouldNotLoadWhenDisabled() {
        contextRunner
            .withPropertyValues("nexora.redis.enabled=false")
            .run(context -> {
                assertThat(context).doesNotHaveBean("cacheManager");
            });
    }

    @Test
    @DisplayName("Should not load without RedisConnectionFactory")
    void shouldNotLoadWithoutRedisConnectionFactory() {
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RedisCacheAutoConfiguration.class))
            .run(context -> {
                assertThat(context).doesNotHaveBean("cacheManager");
            });
    }

    @Test
    @DisplayName("Should configure cache with default TTL")
    void shouldConfigureDefaultTtl() {
        contextRunner
            .withPropertyValues(
                "nexora.redis.enabled=true",
                "nexora.redis.cache-default-ttl=30m"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(CacheManager.class);
                CacheManager cacheManager = context.getBean(CacheManager.class);
                assertThat(cacheManager).isNotNull();
            });
    }

    @Test
    @DisplayName("Should configure per-cache TTL mappings")
    void shouldConfigurePerCacheTtl() {
        contextRunner
            .withPropertyValues(
                "nexora.redis.enabled=true",
                "nexora.redis.cache-ttl-mappings.userCache=10m",
                "nexora.redis.cache-ttl-mappings.productCache=1h"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(CacheManager.class);
            });
    }

    @Test
    @DisplayName("Should configure key prefix")
    void shouldConfigureKeyPrefix() {
        contextRunner
            .withPropertyValues(
                "nexora.redis.enabled=true",
                "nexora.redis.use-cache-prefix=true",
                "nexora.redis.key-prefix=myapp:"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(CacheManager.class);
            });
    }

    @Test
    @DisplayName("Should disable null value caching when configured")
    void shouldDisableNullValueCaching() {
        contextRunner
            .withPropertyValues(
                "nexora.redis.enabled=true",
                "nexora.redis.cache-null-values=false"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(CacheManager.class);
            });
    }

    @Test
    @DisplayName("Should register RedisProperties bean")
    void shouldRegisterRedisProperties() {
        contextRunner
            .withPropertyValues("nexora.redis.enabled=true")
            .run(context -> {
                assertThat(context).hasSingleBean(RedisProperties.class);
            });
    }
}
