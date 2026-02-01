package com.nexora.datajp.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import jakarta.persistence.EntityManagerFactory;
import java.util.Optional;

/**
 * JPA auto-configuration.
 *
 * <p>Features:
 * <ul>
 *   <li>JPA auditing support for {@link com.nexora.datajp.support.BaseEntity}</li>
 *   <li>Default auditor aware implementation</li>
 * </ul>
 *
 * @author sujie
 */
@Slf4j
@Configuration
@ConditionalOnClass(EntityManagerFactory.class)
@EnableConfigurationProperties(JpaProperties.class)
@ConditionalOnProperty(prefix = "nexora.jpa.auditing", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAutoConfiguration {

    public JpaAutoConfiguration(JpaProperties properties) {
        log.info("JPA auto-configuration initialized with auditing enabled: {}",
            properties.getAuditing().isEnabled());
    }

    /**
     * Default auditor aware implementation.
     * Returns an empty optional - users should override this bean
     * to provide actual auditor information (e.g., current user ID).
     */
    @Bean
    @ConditionalOnMissingBean
    public AuditorAware<Long> auditorAware() {
        return () -> Optional.empty();
    }
}
