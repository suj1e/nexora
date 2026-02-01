package com.nexora.audit.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Audit logging configuration properties.
 *
 * <p>Configuration example:
 * <pre>
 * nexora.audit:
 *   enabled: true
 *   async: true
 *   retention-days: 90
 *   sensitive-data:
 *     mask-ip: false
 *     mask-user-agent: true
 * </pre>
 *
 * @author sujie
 */
@Data
@ConfigurationProperties(prefix = "nexora.audit")
public class AuditProperties {

    /**
     * Enable audit logging.
     */
    private boolean enabled = true;

    /**
     * Enable asynchronous logging.
     */
    private boolean async = true;

    /**
     * Number of days to retain audit logs before cleanup.
     */
    private int retentionDays = 90;

    /**
     * Whether to include request/response body in context.
     */
    private boolean includeBodies = false;

    /**
     * Sensitive data masking configuration.
     */
    private SensitiveData sensitiveData = new SensitiveData();

    @Data
    public static class SensitiveData {
        /**
         * Whether to mask IP addresses.
         */
        private boolean maskIp = false;

        /**
         * Whether to mask user agent strings.
         */
        private boolean maskUserAgent = true;

        /**
         * Whether to mask session IDs.
         */
        private boolean maskSessionId = true;
    }
}
