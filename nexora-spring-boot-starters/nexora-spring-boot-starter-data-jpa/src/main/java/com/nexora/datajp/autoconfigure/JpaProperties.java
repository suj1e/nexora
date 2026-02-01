package com.nexora.datajp.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JPA configuration properties.
 *
 * <p>Configuration example:
 * <pre>
 * nexora.jpa:
 *   auditing:
 *     enabled: true
 *   lazy-loading:
 *     enabled: true
 * </pre>
 *
 * @author sujie
 */
@Data
@ConfigurationProperties(prefix = "nexora.jpa")
public class JpaProperties {

    /**
     * JPA auditing configuration.
     */
    private Auditing auditing = new Auditing();

    /**
     * Lazy loading configuration.
     */
    private LazyLoading lazyLoading = new LazyLoading();

    @Data
    public static class Auditing {
        /**
         * Enable JPA auditing.
         */
        private boolean enabled = true;
    }

    @Data
    public static class LazyLoading {
        /**
         * Enable lazy loading.
         */
        private boolean enabled = true;
    }
}
