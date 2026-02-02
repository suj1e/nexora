package com.nexora.audit.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executor;

/**
 * Audit logging auto-configuration.
 *
 * <p>Automatically configures:
 * <ul>
 *   <li>{@link com.nexora.audit.service.AuditLogService}</li>
 * <li>Entity scanning for audit-related entities</li>
 * <li>Repository scanning for audit repositories</li>
 *   <li>Async support for non-blocking audit logging</li>
 *   <li>Scheduled cleanup of old audit logs</li>
 * </ul>
 *
 * @author sujie
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.data.jpa.repository.JpaRepository")
@EnableConfigurationProperties(AuditProperties.class)
@ConditionalOnProperty(prefix = "nexora.audit", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableJpaRepositories(basePackages = "com.nexora.audit.repository")
@EntityScan(basePackages = "com.nexora.audit.domain")
@EnableAsync
@EnableScheduling
public class AuditAutoConfiguration {

    private final AuditProperties properties;

    public AuditAutoConfiguration(AuditProperties properties) {
        this.properties = properties;
        log.info("Audit auto-configuration initialized with async: {}, retention-days: {}",
            properties.isAsync(), properties.getRetentionDays());
    }

    /**
     * Configure async executor for audit logging.
     */
    @Bean
    @ConditionalOnMissingBean(name = "auditTaskExecutor")
    public Executor auditTaskExecutor() {
        // Configure a dedicated thread pool for audit logging
        org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor executor =
            new org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("audit-async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
