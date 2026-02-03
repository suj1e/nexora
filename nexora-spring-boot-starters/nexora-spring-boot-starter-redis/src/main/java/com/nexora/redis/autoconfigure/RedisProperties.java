package com.nexora.redis.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis cache properties with validation.
 *
 * <p>Configuration example for single server mode:
 * <pre>
 * nexora:
 *   redis:
 *     enabled: true
 *     mode: single
 *     single-server:
 *       address: redis://localhost:6379
 *       password: ${REDIS_PASSWORD}
 *       database: 0
 *     connection-pool-size: 64
 *     cache-default-ttl: 30m
 *     use-cache-prefix: true
 *     key-prefix: myapp:
 * </pre>
 *
 * <p>Configuration example for cluster mode:
 * <pre>
 * nexora:
 *   redis:
 *     enabled: true
 *     mode: cluster
 *     cluster-servers:
 *       node-addresses:
 *         - redis://node1:6379
 *         - redis://node2:6379
 *         - redis://node3:6379
 *       password: ${REDIS_PASSWORD}
 * </pre>
 *
 * <p>Configuration example for sentinel mode:
 * <pre>
 * nexora:
 *   redis:
 *     enabled: true
 *     mode: sentinel
 *     sentinel-servers:
 *       sentinel-addresses:
 *         - redis://sentinel1:26379
 *         - redis://sentinel2:26379
 *       master-name: mymaster
 *       password: ${REDIS_PASSWORD}
 * </pre>
 *
 * @author sujie
 * @since 1.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "nexora.redis")
public class RedisProperties {

    /**
     * Enable Redis caching.
     */
    private boolean enabled = true;

    /**
     * Redis connection mode: SINGLE, CLUSTER, SENTINEL, or REPLICATED.
     */
    @NotNull
    private RedisMode mode = RedisMode.SINGLE;

    /**
     * Single server configuration for standalone Redis.
     */
    @Valid
    private SingleServerConfig singleServer = new SingleServerConfig();

    /**
     * Cluster servers configuration for Redis Cluster.
     */
    @Valid
    private ClusterServersConfig clusterServers = new ClusterServersConfig();

    /**
     * Sentinel servers configuration for Redis Sentinel.
     */
    @Valid
    private SentinelServersConfig sentinelServers = new SentinelServersConfig();

    /**
     * Connection pool size for Redis connections.
     */
    @Min(value = 1, message = "Connection pool size must be at least 1")
    private int connectionPoolSize = 64;

    /**
     * Minimum idle connections in the pool.
     */
    @Min(value = 1, message = "Connection minimum idle size must be at least 1")
    private int connectionMinimumIdleSize = 10;

    /**
     * Number of retry attempts when connection fails.
     */
    @Min(value = 0, message = "Retry attempts cannot be negative")
    private int retryAttempts = 3;

    /**
     * Interval between retry attempts in milliseconds.
     */
    @Min(value = 100, message = "Retry interval must be at least 100ms")
    private int retryInterval = 1500;

    /**
     * Default TTL for cache entries. Must be positive.
     */
    @NotNull
    @Min(value = 1, message = "TTL must be at least 1 millisecond")
    private Duration cacheDefaultTtl = Duration.ofMinutes(30);

    /**
     * Cache name to TTL mapping.
     * Format: cache-name:duration,cache-name2:duration2
     * Duration format: 30m, 1h, etc.
     */
    private Map<String, Duration> cacheTtlMappings = new HashMap<>();

    /**
     * Use key prefix for cache entries.
     */
    private boolean useCachePrefix = true;

    /**
     * Key prefix for all cache entries. Must be alphanumeric with allowed special chars.
     */
    @Pattern(regexp = "^[a-zA-Z0-9:_-]*$", message = "Key prefix must be alphanumeric")
    private String keyPrefix = "";

    /**
     * Enable null values caching.
     */
    private boolean cacheNullValues = true;

    /**
     * Use Caffeine as local cache (L1 cache).
     */
    private boolean enableCaffeine = true;

    /**
     * Caffeine cache specification. Cannot be blank.
     */
    @NotBlank(message = "Caffeine spec cannot be blank")
    private String caffeineSpec = "maximumSize=1000,expireAfterWrite=5m";

    /**
     * Redis connection modes.
     */
    public enum RedisMode {
        /**
         * Single standalone Redis server.
         */
        SINGLE,
        /**
         * Redis Cluster with multiple shards.
         */
        CLUSTER,
        /**
         * Redis Sentinel for high availability.
         */
        SENTINEL,
        /**
         * Redis Replicated (cluster-wide) setup.
         */
        REPLICATED
    }

    /**
     * Single server configuration for standalone Redis.
     */
    @Data
    public static class SingleServerConfig {
        /**
         * Redis server address. Format: redis://host:port or rediss://host:port for SSL.
         * Default: redis://localhost:6379
         */
        private String address = "redis://localhost:6379";

        /**
         * Redis password. Leave empty if no authentication.
         */
        private String password = "";

        /**
         * Database number (0-15).
         */
        @Min(value = 0, message = "Database must be between 0 and 15")
        @Max(value = 15, message = "Database must be between 0 and 15")
        private int database = 0;

        /**
         * Connection pool size.
         */
        @Min(value = 1, message = "Connection pool size must be at least 1")
        private int connectionPoolSize = 64;

        /**
         * Minimum idle connections.
         */
        @Min(value = 1, message = "Connection minimum idle size must be at least 1")
        private int connectionMinimumIdleSize = 10;

        /**
         * Subscription connection pool size.
         */
        @Min(value = 1, message = "Subscription connection pool size must be at least 1")
        private int subscriptionConnectionPoolSize = 50;

        /**
         * Timeout in milliseconds for connecting to Redis.
         */
        @Min(value = 100, message = "Connect timeout must be at least 100ms")
        private int connectTimeout = 3000;

        /**
         * Timeout in milliseconds for Redis commands.
         */
        @Min(value = 100, message = "Timeout must be at least 100ms")
        private int timeout = 3000;

        /**
         * Retry attempts for commands.
         */
        @Min(value = 0, message = "Retry attempts cannot be negative")
        private int retryAttempts = 3;

        /**
         * Retry interval in milliseconds.
         */
        @Min(value = 100, message = "Retry interval must be at least 100ms")
        private int retryInterval = 1500;

        /**
         * Reconnection interval in milliseconds.
         */
        @Min(value = 100, message = "Reconnection interval must be at least 100ms")
        private int reconnectionInterval = 3000;

        /**
         * Subscription connection minimum idle size.
         */
        @Min(value = 1, message = "Subscription connection minimum idle size must be at least 1")
        private int subscriptionConnectionMinimumIdleSize = 1;

        /**
         * Keep-alive interval in milliseconds.
         */
        @Min(value = 1000, message = "Keep-alive interval must be at least 1000ms")
        private int keepAliveInterval = 30000;
    }

    /**
     * Cluster servers configuration for Redis Cluster.
     */
    @Data
    public static class ClusterServersConfig {
        /**
         * List of Redis cluster node addresses.
         * Format: redis://host1:port1, redis://host2:port2, ...
         */
        @NotEmpty(message = "At least one cluster node address is required")
        private Map<String, String> nodeAddresses = new HashMap<>();

        /**
         * Redis cluster password.
         */
        private String password = "";

        /**
         * Scan interval for cluster topology update in milliseconds.
         */
        @Min(value = 1000, message = "Scan interval must be at least 1000ms")
        private int scanInterval = 5000;

        /**
         * Number of retries when command fails.
         */
        @Min(value = 0, message = "Retry attempts cannot be negative")
        private int retryAttempts = 3;

        /**
         * Retry interval in milliseconds.
         */
        @Min(value = 100, message = "Retry interval must be at least 100ms")
        private int retryInterval = 1500;

        /**
         * Timeout for node connection in milliseconds.
         */
        @Min(value = 100, message = "Timeout must be at least 100ms")
        private int timeout = 3000;

        /**
         * Connection pool size per node.
         */
        @Min(value = 1, message = "Connection pool size must be at least 1")
        private int connectionPoolSize = 64;

        /**
         * Minimum idle connections per node.
         */
        @Min(value = 1, message = "Connection minimum idle size must be at least 1")
        private int connectionMinimumIdleSize = 10;

        /**
         * Subscription connection pool size per node.
         */
        @Min(value = 1, message = "Subscription connection pool size must be at least 1")
        private int subscriptionConnectionPoolSize = 50;

        /**
         * Subscription connection minimum idle size per node.
         */
        @Min(value = 1, message = "Subscription connection minimum idle size must be at least 1")
        private int subscriptionConnectionMinimumIdleSize = 1;
    }

    /**
     * Sentinel servers configuration for Redis Sentinel.
     */
    @Data
    public static class SentinelServersConfig {
        /**
         * List of Redis sentinel addresses.
         * Format: redis://sentinel1:port1, redis://sentinel2:port2, ...
         */
        @NotEmpty(message = "At least one sentinel address is required")
        private Map<String, String> sentinelAddresses = new HashMap<>();

        /**
         * Master name monitored by sentinel. Default: mymaster.
         */
        @NotBlank(message = "Master name is required")
        private String masterName = "mymaster";

        /**
         * Redis password.
         */
        private String password = "";

        /**
         * Database number (0-15).
         */
        @Min(value = 0, message = "Database must be between 0 and 15")
        @Max(value = 15, message = "Database must be between 0 and 15")
        private int database = 0;

        /**
         * Scan interval for sentinel topology update in milliseconds.
         */
        @Min(value = 1000, message = "Scan interval must be at least 1000ms")
        private int scanInterval = 5000;

        /**
         * Number of retries when command fails.
         */
        @Min(value = 0, message = "Retry attempts cannot be negative")
        private int retryAttempts = 3;

        /**
         * Retry interval in milliseconds.
         */
        @Min(value = 100, message = "Retry interval must be at least 100ms")
        private int retryInterval = 1500;

        /**
         * Timeout for connection in milliseconds.
         */
        @Min(value = 100, message = "Timeout must be at least 100ms")
        private int timeout = 3000;

        /**
         * Connection pool size.
         */
        @Min(value = 1, message = "Connection pool size must be at least 1")
        private int connectionPoolSize = 64;

        /**
         * Minimum idle connections.
         */
        @Min(value = 1, message = "Connection minimum idle size must be at least 1")
        private int connectionMinimumIdleSize = 10;

        /**
         * Subscription connection pool size.
         */
        @Min(value = 1, message = "Subscription connection pool size must be at least 1")
        private int subscriptionConnectionPoolSize = 50;

        /**
         * Subscription connection minimum idle size.
         */
        @Min(value = 1, message = "Subscription connection minimum idle size must be at least 1")
        private int subscriptionConnectionMinimumIdleSize = 1;

        /**
         * Reconnection interval in milliseconds.
         */
        @Min(value = 100, message = "Reconnection interval must be at least 100ms")
        private int reconnectionInterval = 3000;
    }
}
