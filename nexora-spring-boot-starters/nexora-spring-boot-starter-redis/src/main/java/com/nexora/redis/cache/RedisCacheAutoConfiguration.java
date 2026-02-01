package com.nexora.redis.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis cache auto-configuration.
 *
 * <p>Features:
 * <ul>
 *   <li>JSON serialization with Jackson</li>
 *   <li>Configurable TTL per cache</li>
 *   <li>Key prefix support</li>
 *   <li>Null values caching</li>
 * </ul>
 *
 * @author sujie
 */
@Slf4j
@Configuration
@ConditionalOnClass(RedisConnectionFactory.class)
@EnableCaching
@EnableConfigurationProperties(RedisProperties.class)
@ConditionalOnProperty(prefix = "nexora.redis", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RedisCacheAutoConfiguration {

    /**
     * ObjectMapper bean for Redis JSON serialization with JavaTimeModule support.
     * Created only if not already provided by the application.
     */
    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper redisObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModules(new JavaTimeModule());
        log.debug("Created ObjectMapper for Redis cache with JavaTimeModule");
        return objectMapper;
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            RedisProperties properties,
            ObjectMapper objectMapper
    ) {
        log.info("Initializing RedisCacheManager with default TTL: {}", properties.getCacheDefaultTtl());

        // Redis cache configuration
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(properties.getCacheDefaultTtl())
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()
                        )
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer(objectMapper)
                        )
                );

        // Configure null values caching
        if (!properties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }

        // Configure key prefix
        if (properties.isUseCachePrefix() && !properties.getKeyPrefix().isEmpty()) {
            config = config.prefixCacheNameWith(properties.getKeyPrefix());
        }

        // Per-cache TTL configuration
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        for (Map.Entry<String, Duration> entry : properties.getCacheTtlMappings().entrySet()) {
            cacheConfigurations.put(
                    entry.getKey(),
                    config.entryTtl(entry.getValue())
            );
        }

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}
