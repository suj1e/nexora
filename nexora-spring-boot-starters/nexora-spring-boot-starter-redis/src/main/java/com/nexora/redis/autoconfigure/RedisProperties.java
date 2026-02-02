package com.nexora.redis.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.*;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis cache properties with validation.
 *
 * <p>Configuration example:
 * <pre>
 * nexora.redis.enabled=true
 * nexora.redis.cache-default-ttl=30m
 * nexora.redis.cache-names=user:10m,role:5m,token-blacklist:30m
 * nexora.redis.use-cache-prefix=true
 * nexora.redis.key-prefix=myapp:
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
}
