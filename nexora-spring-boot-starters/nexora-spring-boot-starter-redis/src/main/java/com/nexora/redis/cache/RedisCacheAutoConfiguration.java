package com.nexora.redis.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nexora.redis.autoconfigure.RedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
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
 * Redis cache auto-configuration using Redisson.
 *
 * <p>Features:
 * <ul>
 *   <li>JSON serialization with Jackson</li>
 *   <li>Configurable TTL per cache</li>
 *   <li>Key prefix support</li>
 *   <li>Null values caching</li>
 *   <li>Single, Cluster, Sentinel, and Replicated modes support</li>
 * </ul>
 *
 * @author sujie
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@EnableCaching
@EnableConfigurationProperties(RedisProperties.class)
@ConditionalOnClass(Redisson.class)
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

    /**
     * Creates and configures the RedissonClient based on the specified mode.
     * Supports SINGLE, CLUSTER, SENTINEL, and REPLICATED modes.
     */
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedissonClient redissonClient(RedisProperties properties) {
        Config config = new Config();

        log.info("Initializing RedissonClient with mode: {}", properties.getMode());

        switch (properties.getMode()) {
            case SINGLE -> configureSingleServer(config, properties);
            case CLUSTER -> configureClusterServers(config, properties);
            case SENTINEL -> configureSentinelServers(config, properties);
            case REPLICATED -> configureReplicatedServers(config, properties);
            default -> throw new IllegalArgumentException("Unknown Redis mode: " + properties.getMode());
        }

        return Redisson.create(config);
    }

    /**
     * Configure single server mode for standalone Redis.
     */
    private void configureSingleServer(Config config, RedisProperties properties) {
        RedisProperties.SingleServerConfig serverConfig = properties.getSingleServer();

        log.info("Configuring Redisson single server: {}", serverConfig.getAddress());

        SingleServerConfig singleServerConfig = config.useSingleServer()
                .setAddress(serverConfig.getAddress())
                .setDatabase(serverConfig.getDatabase())
                .setConnectionPoolSize(serverConfig.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(serverConfig.getConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(serverConfig.getSubscriptionConnectionPoolSize())
                .setConnectTimeout(serverConfig.getConnectTimeout())
                .setTimeout(serverConfig.getTimeout())
                .setRetryAttempts(serverConfig.getRetryAttempts())
                .setRetryInterval(serverConfig.getRetryInterval())
                .setSubscriptionConnectionMinimumIdleSize(serverConfig.getSubscriptionConnectionMinimumIdleSize());

        if (serverConfig.getPassword() != null && !serverConfig.getPassword().isEmpty()) {
            singleServerConfig.setPassword(serverConfig.getPassword());
        }
    }

    /**
     * Configure cluster servers mode for Redis Cluster.
     */
    private void configureClusterServers(Config config, RedisProperties properties) {
        RedisProperties.ClusterServersConfig serverConfig = properties.getClusterServers();

        if (serverConfig.getNodeAddresses().isEmpty()) {
            throw new IllegalArgumentException("Cluster node addresses cannot be empty in CLUSTER mode");
        }

        String[] addresses = serverConfig.getNodeAddresses().values().toArray(new String[0]);
        log.info("Configuring Redisson cluster with {} nodes", addresses.length);

        ClusterServersConfig clusterConfig = config.useClusterServers()
                .addNodeAddress(addresses)
                .setScanInterval(serverConfig.getScanInterval())
                .setRetryAttempts(serverConfig.getRetryAttempts())
                .setRetryInterval(serverConfig.getRetryInterval())
                .setTimeout(serverConfig.getTimeout())
                .setConnectTimeout(serverConfig.getTimeout())
                .setSubscriptionConnectionPoolSize(serverConfig.getSubscriptionConnectionPoolSize())
                .setSubscriptionConnectionMinimumIdleSize(serverConfig.getSubscriptionConnectionMinimumIdleSize());

        if (serverConfig.getPassword() != null && !serverConfig.getPassword().isEmpty()) {
            clusterConfig.setPassword(serverConfig.getPassword());
        }
    }

    /**
     * Configure sentinel servers mode for Redis Sentinel.
     */
    private void configureSentinelServers(Config config, RedisProperties properties) {
        RedisProperties.SentinelServersConfig serverConfig = properties.getSentinelServers();

        if (serverConfig.getSentinelAddresses().isEmpty()) {
            throw new IllegalArgumentException("Sentinel addresses cannot be empty in SENTINEL mode");
        }

        String[] addresses = serverConfig.getSentinelAddresses().values().toArray(new String[0]);
        log.info("Configuring Redisson sentinel with master '{}' and {} sentinels",
                serverConfig.getMasterName(), addresses.length);

        SentinelServersConfig sentinelConfig = config.useSentinelServers()
                .addSentinelAddress(addresses)
                .setMasterName(serverConfig.getMasterName())
                .setDatabase(serverConfig.getDatabase())
                .setScanInterval(serverConfig.getScanInterval())
                .setRetryAttempts(serverConfig.getRetryAttempts())
                .setRetryInterval(serverConfig.getRetryInterval())
                .setTimeout(serverConfig.getTimeout())
                .setConnectTimeout(serverConfig.getTimeout())
                .setSubscriptionConnectionPoolSize(serverConfig.getSubscriptionConnectionPoolSize())
                .setSubscriptionConnectionMinimumIdleSize(serverConfig.getSubscriptionConnectionMinimumIdleSize());

        if (serverConfig.getPassword() != null && !serverConfig.getPassword().isEmpty()) {
            sentinelConfig.setPassword(serverConfig.getPassword());
        }
    }

    /**
     * Configure replicated servers mode for Redis Replicated (cluster-wide) setup.
     * Uses cluster configuration but with replicated mode enabled.
     */
    private void configureReplicatedServers(Config config, RedisProperties properties) {
        RedisProperties.ClusterServersConfig serverConfig = properties.getClusterServers();

        if (serverConfig.getNodeAddresses().isEmpty()) {
            throw new IllegalArgumentException("Node addresses cannot be empty in REPLICATED mode");
        }

        String[] addresses = serverConfig.getNodeAddresses().values().toArray(new String[0]);
        log.info("Configuring Redisson replicated mode with {} nodes", addresses.length);

        org.redisson.config.ReplicatedServersConfig replicatedConfig = config.useReplicatedServers()
                .addNodeAddress(addresses)
                .setScanInterval(serverConfig.getScanInterval())
                .setRetryAttempts(serverConfig.getRetryAttempts())
                .setRetryInterval(serverConfig.getRetryInterval())
                .setTimeout(serverConfig.getTimeout())
                .setConnectTimeout(serverConfig.getTimeout())
                .setSubscriptionConnectionPoolSize(serverConfig.getSubscriptionConnectionPoolSize())
                .setSubscriptionConnectionMinimumIdleSize(serverConfig.getSubscriptionConnectionMinimumIdleSize());

        if (serverConfig.getPassword() != null && !serverConfig.getPassword().isEmpty()) {
            replicatedConfig.setPassword(serverConfig.getPassword());
        }
    }

    /**
     * Creates RedisConnectionFactory using Redisson.
     */
    @Bean
    @ConditionalOnMissingBean
    public RedisConnectionFactory redisConnectionFactory(RedissonClient redissonClient) {
        return new RedissonConnectionFactory(redissonClient);
    }

    /**
     * Creates CacheManager using Spring Data Redis's RedisCacheManager with Redisson connection factory.
     * Configures TTL per cache, key prefix, and null value caching.
     */
    @Bean
    @ConditionalOnMissingBean
    @SuppressWarnings("removal")
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
            log.debug("Cache '{}' configured with TTL: {}", entry.getKey(), entry.getValue());
        }

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}
