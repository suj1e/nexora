package com.nexora.redis.autoconfigure;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine local cache auto-configuration.
 *
 * <p>Features:
 * <ul>
 *   <li>High-performance in-memory caching</li>
 *   <li>Configurable size and TTL</li>
 *   <li>Automatic eviction based on LRU</li>
 * </ul>
 *
 * @author sujie
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(Caffeine.class)
@EnableCaching
@EnableConfigurationProperties(RedisProperties.class)
@ConditionalOnProperty(prefix = "nexora.redis", name = "enable-caffeine", havingValue = "true", matchIfMissing = true)
public class CaffeineAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CaffeineCacheManager caffeineCacheManager(RedisProperties properties) {
        log.info("Initializing CaffeineCacheManager with spec: {}", properties.getCaffeineSpec());

        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // Parse Caffeine spec: maximumSize=1000,expireAfterWrite=5m
        Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder();

        String[] parts = properties.getCaffeineSpec().split(",");
        for (String part : parts) {
            String[] kv = part.split("=");
            if (kv.length == 2) {
                String key = kv[0].trim();
                String value = kv[1].trim();

                // Java 21 Switch Expression
                switch (key) {
                    case "maximumSize" -> caffeineBuilder.maximumSize(Long.parseLong(value));
                    case "expireAfterWrite" -> caffeineBuilder.expireAfterWrite(parseDuration(value), TimeUnit.MILLISECONDS);
                    case "expireAfterAccess" -> caffeineBuilder.expireAfterAccess(parseDuration(value), TimeUnit.MILLISECONDS);
                    default -> { /* ignore unknown keys */ }
                }
            }
        }

        cacheManager.setCaffeine(caffeineBuilder);
        return cacheManager;
    }

    /**
     * Parse duration string to milliseconds.
     * Uses Java 21 switch expression for pattern matching.
     *
     * @param duration the duration string (e.g., "100ms", "5s", "10m", "1h")
     * @return duration in milliseconds
     */
    private long parseDuration(String duration) {
        String lowerDuration = duration.toLowerCase();

        return switch (extractSuffix(lowerDuration)) {
            case "ms" -> Long.parseLong(lowerDuration.substring(0, lowerDuration.length() - 2));
            case "s" -> Long.parseLong(lowerDuration.substring(0, lowerDuration.length() - 1)) * 1000;
            case "m" -> Long.parseLong(lowerDuration.substring(0, lowerDuration.length() - 1)) * 60 * 1000;
            case "h" -> Long.parseLong(lowerDuration.substring(0, lowerDuration.length() - 1)) * 60 * 60 * 1000;
            default -> Long.parseLong(lowerDuration); // plain number, treat as milliseconds
        };
    }

    /**
     * Extract suffix from duration string.
     *
     * @param duration the duration string
     * @return the suffix (ms, s, m, h) or empty string
     */
    private String extractSuffix(String duration) {
        if (duration.endsWith("ms")) return "ms";
        if (duration.endsWith("s")) return "s";
        if (duration.endsWith("m")) return "m";
        if (duration.endsWith("h")) return "h";
        return "";
    }
}
