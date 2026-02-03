package com.nexora.redis.autoconfigure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link RedisProperties}.
 */
@DisplayName("RedisProperties Tests")
class RedisPropertiesTest {

    @Test
    @DisplayName("Default properties should have expected default values")
    void testDefaultProperties() {
        RedisProperties properties = new RedisProperties();

        assertAll("Default Redis properties",
            () -> assertTrue(properties.isEnabled(), "Should be enabled by default"),
            () -> assertEquals(RedisProperties.RedisMode.SINGLE, properties.getMode(),
                "Default mode should be SINGLE"),
            () -> assertEquals(Duration.ofMinutes(30), properties.getCacheDefaultTtl(),
                "Default TTL should be 30 minutes"),
            () -> assertTrue(properties.isUseCachePrefix(), "Should use cache prefix by default"),
            () -> assertEquals("", properties.getKeyPrefix(), "Key prefix should be empty by default"),
            () -> assertTrue(properties.isCacheNullValues(), "Should cache null values by default"),
            () -> assertTrue(properties.isEnableCaffeine(), "Caffeine should be enabled by default"),
            () -> assertEquals("maximumSize=1000,expireAfterWrite=5m", properties.getCaffeineSpec(),
                "Caffeine spec should have default value"),
            () -> assertEquals(64, properties.getConnectionPoolSize(),
                "Connection pool size should be 64"),
            () -> assertEquals(10, properties.getConnectionMinimumIdleSize(),
                "Connection minimum idle size should be 10"),
            () -> assertEquals(3, properties.getRetryAttempts(),
                "Retry attempts should be 3"),
            () -> assertEquals(1500, properties.getRetryInterval(),
                "Retry interval should be 1500ms")
        );
    }

    @Test
    @DisplayName("Cache TTL mappings should be mutable")
    void testCacheTtlMappings() {
        RedisProperties properties = new RedisProperties();
        Map<String, Duration> mappings = new HashMap<>();
        mappings.put("userCache", Duration.ofMinutes(10));
        mappings.put("roleCache", Duration.ofHours(1));

        properties.setCacheTtlMappings(mappings);

        assertAll("Cache TTL mappings",
            () -> assertEquals(2, properties.getCacheTtlMappings().size()),
            () -> assertEquals(Duration.ofMinutes(10), properties.getCacheTtlMappings().get("userCache")),
            () -> assertEquals(Duration.ofHours(1), properties.getCacheTtlMappings().get("roleCache"))
        );
    }

    @Test
    @DisplayName("Should be able to set all properties")
    void testSetAllProperties() {
        RedisProperties properties = new RedisProperties();
        Map<String, Duration> mappings = new HashMap<>();
        mappings.put("test", Duration.ofMinutes(5));

        properties.setEnabled(false);
        properties.setMode(RedisProperties.RedisMode.CLUSTER);
        properties.setCacheDefaultTtl(Duration.ofHours(1));
        properties.setCacheTtlMappings(mappings);
        properties.setUseCachePrefix(false);
        properties.setKeyPrefix("custom:");
        properties.setCacheNullValues(false);
        properties.setEnableCaffeine(false);
        properties.setCaffeineSpec("maximumSize=500");
        properties.setConnectionPoolSize(128);
        properties.setConnectionMinimumIdleSize(20);
        properties.setRetryAttempts(5);
        properties.setRetryInterval(2000);

        assertAll("Custom properties",
            () -> assertFalse(properties.isEnabled()),
            () -> assertEquals(RedisProperties.RedisMode.CLUSTER, properties.getMode()),
            () -> assertEquals(Duration.ofHours(1), properties.getCacheDefaultTtl()),
            () -> assertFalse(properties.isUseCachePrefix()),
            () -> assertEquals("custom:", properties.getKeyPrefix()),
            () -> assertFalse(properties.isCacheNullValues()),
            () -> assertFalse(properties.isEnableCaffeine()),
            () -> assertEquals("maximumSize=500", properties.getCaffeineSpec()),
            () -> assertEquals(128, properties.getConnectionPoolSize()),
            () -> assertEquals(20, properties.getConnectionMinimumIdleSize()),
            () -> assertEquals(5, properties.getRetryAttempts()),
            () -> assertEquals(2000, properties.getRetryInterval())
        );
    }

    @Test
    @DisplayName("Cache TTL mappings should initialize as empty map")
    void testCacheTtlMappingsInitialization() {
        RedisProperties properties = new RedisProperties();

        assertNotNull(properties.getCacheTtlMappings(), "Cache TTL mappings should not be null");
        assertTrue(properties.getCacheTtlMappings().isEmpty(), "Cache TTL mappings should be empty by default");
    }

    @Test
    @DisplayName("SingleServerConfig should have default values")
    void testSingleServerConfigDefaults() {
        RedisProperties.SingleServerConfig config = new RedisProperties.SingleServerConfig();

        assertAll("SingleServerConfig defaults",
            () -> assertEquals("redis://localhost:6379", config.getAddress()),
            () -> assertEquals("", config.getPassword()),
            () -> assertEquals(0, config.getDatabase()),
            () -> assertEquals(64, config.getConnectionPoolSize()),
            () -> assertEquals(10, config.getConnectionMinimumIdleSize()),
            () -> assertEquals(50, config.getSubscriptionConnectionPoolSize()),
            () -> assertEquals(3000, config.getConnectTimeout()),
            () -> assertEquals(3000, config.getTimeout()),
            () -> assertEquals(3, config.getRetryAttempts()),
            () -> assertEquals(1500, config.getRetryInterval()),
            () -> assertEquals(3000, config.getReconnectionInterval()),
            () -> assertEquals(1, config.getSubscriptionConnectionMinimumIdleSize()),
            () -> assertEquals(30000, config.getKeepAliveInterval())
        );
    }

    @Test
    @DisplayName("ClusterServersConfig should have default values")
    void testClusterServersConfigDefaults() {
        RedisProperties.ClusterServersConfig config = new RedisProperties.ClusterServersConfig();

        assertAll("ClusterServersConfig defaults",
            () -> assertTrue(config.getNodeAddresses().isEmpty()),
            () -> assertEquals("", config.getPassword()),
            () -> assertEquals(5000, config.getScanInterval()),
            () -> assertEquals(3, config.getRetryAttempts()),
            () -> assertEquals(1500, config.getRetryInterval()),
            () -> assertEquals(3000, config.getTimeout()),
            () -> assertEquals(64, config.getConnectionPoolSize()),
            () -> assertEquals(10, config.getConnectionMinimumIdleSize()),
            () -> assertEquals(50, config.getSubscriptionConnectionPoolSize()),
            () -> assertEquals(1, config.getSubscriptionConnectionMinimumIdleSize())
        );
    }

    @Test
    @DisplayName("SentinelServersConfig should have default values")
    void testSentinelServersConfigDefaults() {
        RedisProperties.SentinelServersConfig config = new RedisProperties.SentinelServersConfig();

        assertAll("SentinelServersConfig defaults",
            () -> assertTrue(config.getSentinelAddresses().isEmpty()),
            () -> assertEquals("mymaster", config.getMasterName()),
            () -> assertEquals("", config.getPassword()),
            () -> assertEquals(0, config.getDatabase()),
            () -> assertEquals(5000, config.getScanInterval()),
            () -> assertEquals(3, config.getRetryAttempts()),
            () -> assertEquals(1500, config.getRetryInterval()),
            () -> assertEquals(3000, config.getTimeout()),
            () -> assertEquals(64, config.getConnectionPoolSize()),
            () -> assertEquals(10, config.getConnectionMinimumIdleSize()),
            () -> assertEquals(50, config.getSubscriptionConnectionPoolSize()),
            () -> assertEquals(1, config.getSubscriptionConnectionMinimumIdleSize()),
            () -> assertEquals(3000, config.getReconnectionInterval())
        );
    }

    @Test
    @DisplayName("All RedisMode enum values should be accessible")
    void testRedisModeEnum() {
        assertAll("RedisMode enum values",
            () -> assertEquals(RedisProperties.RedisMode.SINGLE, RedisProperties.RedisMode.valueOf("SINGLE")),
            () -> assertEquals(RedisProperties.RedisMode.CLUSTER, RedisProperties.RedisMode.valueOf("CLUSTER")),
            () -> assertEquals(RedisProperties.RedisMode.SENTINEL, RedisProperties.RedisMode.valueOf("SENTINEL")),
            () -> assertEquals(RedisProperties.RedisMode.REPLICATED, RedisProperties.RedisMode.valueOf("REPLICATED"))
        );
    }
}
