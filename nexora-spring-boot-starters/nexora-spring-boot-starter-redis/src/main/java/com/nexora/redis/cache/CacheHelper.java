package com.nexora.redis.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Cache helper utility with improved type safety.
 *
 * <p>Provides convenient methods for cache operations with fallback logic.
 *
 * @author sujie
 */
public class CacheHelper {

    private final CacheManager cacheManager;

    public CacheHelper(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Get value from cache, or compute and cache if not present.
     *
     * @param cacheName the cache name
     * @param key       the cache key
     * @param type      the expected type of the cached value
     * @param loader    the value loader
     * @param <T>       the value type
     * @return the cached or computed value
     */
    public <T> T getOrCompute(String cacheName, Object key, Class<T> type, Supplier<T> loader) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return loader.get();
        }

        Cache.ValueWrapper wrapper = cache.get(key);
        if (wrapper != null && wrapper.get() != null) {
            Object value = wrapper.get();
            if (type.isInstance(value)) {
                return type.cast(value);
            }
            // Type mismatch, fall through to reload
        }

        T value = loader.get();
        if (value != null) {
            cache.put(key, value);
        }
        return value;
    }

    /**
     * Get value from cache, or compute and cache if not present.
     * This method uses type inference but may throw ClassCastException on type mismatch.
     *
     * @param cacheName the cache name
     * @param key       the cache key
     * @param loader    the value loader
     * @param <T>       the value type
     * @return the cached or computed value
     * @deprecated Use {@link #getOrCompute(String, Object, Class, Supplier)} for better type safety
     */
    @Deprecated(since = "1.1", forRemoval = false)
    @SuppressWarnings("unchecked")
    public <T> T getOrCompute(String cacheName, Object key, Supplier<T> loader) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return loader.get();
        }

        Cache.ValueWrapper wrapper = cache.get(key);
        if (wrapper != null) {
            return (T) wrapper.get();
        }

        T value = loader.get();
        cache.put(key, value);
        return value;
    }

    /**
     * Get value from cache as Optional.
     *
     * @param cacheName the cache name
     * @param key       the cache key
     * @param type      the expected type of the cached value
     * @param <T>       the value type
     * @return Optional containing the value, or empty if not found
     */
    public <T> Optional<T> getOptional(String cacheName, Object key, Class<T> type) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return Optional.empty();
        }

        Cache.ValueWrapper wrapper = cache.get(key);
        if (wrapper != null && wrapper.get() != null && type.isInstance(wrapper.get())) {
            return Optional.of(type.cast(wrapper.get()));
        }
        return Optional.empty();
    }

    /**
     * Get value from cache as Optional.
     * This method uses type inference but may throw ClassCastException on type mismatch.
     *
     * @param cacheName the cache name
     * @param key       the cache key
     * @param <T>       the value type
     * @return Optional containing the value, or empty if not found
     * @deprecated Use {@link #getOptional(String, Object, Class)} for better type safety
     */
    @Deprecated(since = "1.1", forRemoval = false)
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getOptional(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return Optional.empty();
        }

        Cache.ValueWrapper wrapper = cache.get(key);
        return wrapper != null
                ? Optional.ofNullable((T) wrapper.get())
                : Optional.empty();
    }

    /**
     * Get a value from cache directly.
     *
     * @param cacheName the cache name
     * @param key       the cache key
     * @param type      the expected type of the cached value
     * @param <T>       the value type
     * @return the cached value, or null if not found
     */
    public <T> T get(String cacheName, Object key, Class<T> type) {
        return getOptional(cacheName, key, type).orElse(null);
    }

    /**
     * Evict entry from cache.
     *
     * @param cacheName the cache name
     * @param key       the cache key
     */
    public void evict(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    /**
     * Clear all entries in cache.
     *
     * @param cacheName the cache name
     */
    public void clear(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    /**
     * Put a value into cache.
     *
     * @param cacheName the cache name
     * @param key       the cache key
     * @param value     the value to cache
     * @param <T>       the value type
     */
    public <T> void put(String cacheName, Object key, T value) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.put(key, value);
        }
    }

    /**
     * Check if a key exists in the cache.
     *
     * @param cacheName the cache name
     * @param key       the cache key
     * @return true if the key exists in cache, false otherwise
     */
    public boolean exists(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return false;
        }
        Cache.ValueWrapper wrapper = cache.get(key);
        return wrapper != null && wrapper.get() != null;
    }
}
