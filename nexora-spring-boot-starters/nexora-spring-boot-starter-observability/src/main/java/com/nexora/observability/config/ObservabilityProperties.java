package com.nexora.observability.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Observability configuration properties.
 *
 * @author sujie
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "nexora.observability")
public class ObservabilityProperties {

    /**
     * Enable observability auto-configuration.
     */
    private boolean enabled = true;

    /**
     * Application name for metrics tagging.
     */
    private String applicationName = "application";

    /**
     * Common tags configuration.
     */
    private CommonTags commonTags = new CommonTags();

    @Data
    public static class CommonTags {
        /**
         * Additional common tags to apply to all metrics.
         */
        private java.util.Map<String, String> tags = new java.util.HashMap<>();
    }
}
