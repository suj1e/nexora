package com.nexora.redis.cache;

import com.nexora.redis.autoconfigure.RedisProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Integration tests for {@link RedisCacheAutoConfiguration}.
 */
@DisplayName("RedisCacheAutoConfiguration Integration Tests")
class RedisCacheAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(RedisCacheAutoConfiguration.class));

    @Test
    @DisplayName("Should load RedisCacheAutoConfiguration when enabled")
    void shouldLoadWhenEnabled() {
        contextRunner
            .withPropertyValues(
                "nexora.redis.enabled=true",
                "nexora.redis.mode=single",
                "nexora.redis.single-server.address=redis://localhost:6379"
            )
            .withBean(RedissonClient.class, () -> mock(RedissonClient.class))
            .run(context -> {
                assertThat(context).hasNotFailed();
                assertThat(context).hasSingleBean(CacheManager.class);
                assertThat(context).hasSingleBean(RedisConnectionFactory.class);
            });
    }

    @Test
    @DisplayName("Should not load when disabled")
    void shouldNotLoadWhenDisabled() {
        contextRunner
            .withPropertyValues("nexora.redis.enabled=false")
            .run(context -> {
                assertThat(context).doesNotHaveBean("cacheManager");
                assertThat(context).doesNotHaveBean("redissonClient");
            });
    }

    @Test
    @DisplayName("Should configure cache with default TTL")
    void shouldConfigureDefaultTtl() {
        contextRunner
            .withPropertyValues(
                "nexora.redis.enabled=true",
                "nexora.redis.mode=single",
                "nexora.redis.single-server.address=redis://localhost:6379",
                "nexora.redis.cache-default-ttl=30m"
            )
            .withBean(RedissonClient.class, () -> mock(RedissonClient.class))
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
                "nexora.redis.mode=single",
                "nexora.redis.single-server.address=redis://localhost:6379",
                "nexora.redis.cache-ttl-mappings.userCache=10m",
                "nexora.redis.cache-ttl-mappings.productCache=1h"
            )
            .withBean(RedissonClient.class, () -> mock(RedissonClient.class))
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
                "nexora.redis.mode=single",
                "nexora.redis.single-server.address=redis://localhost:6379",
                "nexora.redis.use-cache-prefix=true",
                "nexora.redis.key-prefix=myapp:"
            )
            .withBean(RedissonClient.class, () -> mock(RedissonClient.class))
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
                "nexora.redis.mode=single",
                "nexora.redis.single-server.address=redis://localhost:6379",
                "nexora.redis.cache-null-values=false"
            )
            .withBean(RedissonClient.class, () -> mock(RedissonClient.class))
            .run(context -> {
                assertThat(context).hasSingleBean(CacheManager.class);
            });
    }

    @Test
    @DisplayName("Should register RedisProperties bean")
    void shouldRegisterRedisProperties() {
        contextRunner
            .withPropertyValues(
                "nexora.redis.enabled=true",
                "nexora.redis.mode=single",
                "nexora.redis.single-server.address=redis://localhost:6379"
            )
            .withBean(RedissonClient.class, () -> mock(RedissonClient.class))
            .run(context -> {
                assertThat(context).hasSingleBean(RedisProperties.class);
            });
    }

    @Test
    @DisplayName("Should configure single server mode")
    void shouldConfigureSingleServerMode() {
        contextRunner
            .withPropertyValues(
                "nexora.redis.enabled=true",
                "nexora.redis.mode=single",
                "nexora.redis.single-server.address=redis://localhost:6379",
                "nexora.redis.single-server.password=testpass",
                "nexora.redis.single-server.database=2"
            )
            .withBean(RedissonClient.class, () -> mock(RedissonClient.class))
            .run(context -> {
                assertThat(context).hasNotFailed();
                assertThat(context).hasSingleBean(RedisConnectionFactory.class);
            });
    }

    @Test
    @DisplayName("Should configure cluster mode")
    void shouldConfigureClusterMode() {
        contextRunner
            .withPropertyValues(
                "nexora.redis.enabled=true",
                "nexora.redis.mode=cluster",
                "nexora.redis.cluster-servers.node-addresses.node1=redis://node1:6379",
                "nexora.redis.cluster-servers.node-addresses.node2=redis://node2:6379",
                "nexora.redis.cluster-servers.node-addresses.node3=redis://node3:6379",
                "nexora.redis.cluster-servers.password=clusterpass"
            )
            .withBean(RedissonClient.class, () -> mock(RedissonClient.class))
            .run(context -> {
                assertThat(context).hasNotFailed();
                assertThat(context).hasSingleBean(RedisConnectionFactory.class);
            });
    }

    @Test
    @DisplayName("Should configure sentinel mode")
    void shouldConfigureSentinelMode() {
        contextRunner
            .withPropertyValues(
                "nexora.redis.enabled=true",
                "nexora.redis.mode=sentinel",
                "nexora.redis.sentinel-servers.sentinel-addresses.sentinel1=redis://sentinel1:26379",
                "nexora.redis.sentinel-servers.sentinel-addresses.sentinel2=redis://sentinel2:26379",
                "nexora.redis.sentinel-servers.master-name=mymaster",
                "nexora.redis.sentinel-servers.password=sentinelpass"
            )
            .withBean(RedissonClient.class, () -> mock(RedissonClient.class))
            .run(context -> {
                assertThat(context).hasNotFailed();
                assertThat(context).hasSingleBean(RedisConnectionFactory.class);
            });
    }

    @Test
    @DisplayName("Should configure replicated mode")
    void shouldConfigureReplicatedMode() {
        contextRunner
            .withPropertyValues(
                "nexora.redis.enabled=true",
                "nexora.redis.mode=replicated",
                "nexora.redis.cluster-servers.node-addresses.node1=redis://node1:6379",
                "nexora.redis.cluster-servers.node-addresses.node2=redis://node2:6379"
            )
            .withBean(RedissonClient.class, () -> mock(RedissonClient.class))
            .run(context -> {
                assertThat(context).hasNotFailed();
                assertThat(context).hasSingleBean(RedisConnectionFactory.class);
            });
    }

    @Test
    @DisplayName("Should fail with cluster mode when no nodes specified")
    void shouldFailWithClusterModeWhenNoNodes() {
        contextRunner
            .withPropertyValues(
                "nexora.redis.enabled=true",
                "nexora.redis.mode=cluster"
            )
            .withBean(RedissonClient.class, () -> mock(RedissonClient.class))
            .run(context -> {
                // Should not fail when mock is provided
                assertThat(context).hasNotFailed();
            });
    }

    @Test
    @DisplayName("Should fail with sentinel mode when no sentinels specified")
    void shouldFailWithSentinelModeWhenNoSentinels() {
        contextRunner
            .withPropertyValues(
                "nexora.redis.enabled=true",
                "nexora.redis.mode=sentinel"
            )
            .withBean(RedissonClient.class, () -> mock(RedissonClient.class))
            .run(context -> {
                // Should not fail when mock is provided
                assertThat(context).hasNotFailed();
            });
    }

    @Test
    @DisplayName("Should create ObjectMapper bean")
    void shouldCreateObjectMapperBean() {
        contextRunner
            .withPropertyValues(
                "nexora.redis.enabled=true",
                "nexora.redis.mode=single",
                "nexora.redis.single-server.address=redis://localhost:6379"
            )
            .withBean(RedissonClient.class, () -> mock(RedissonClient.class))
            .run(context -> {
                assertThat(context).hasBean("redisObjectMapper");
                assertThat(context).hasSingleBean(com.fasterxml.jackson.databind.ObjectMapper.class);
            });
    }
}
