package com.nexora.observability.autoconfigure;

import com.nexora.observability.config.ObservabilityProperties;
import com.nexora.observability.util.MetricsUtil;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Observability auto-configuration.
 *
 * <p>Automatically configures:
 * <ul>
 *   <li>Micrometer metrics utilities</li>
 *   <li>Common tags for all metrics</li>
 *   <li>Application name tagging</li>
 * </ul>
 *
 * @author sujie
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(name = "io.micrometer.core.instrument.MeterRegistry")
@EnableConfigurationProperties(ObservabilityProperties.class)
@ConditionalOnProperty(prefix = "nexora.observability", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ObservabilityAutoConfiguration {

    private final ObservabilityProperties properties;

    public ObservabilityAutoConfiguration(ObservabilityProperties properties) {
        this.properties = properties;
        log.info("Observability auto-configuration initialized with application: {}",
            properties.getApplicationName());
    }

    /**
     * Metrics utility bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public MetricsUtil metricsUtil(MeterRegistry meterRegistry) {
        return new MetricsUtil(meterRegistry);
    }

    /**
     * Configure common tags for all metrics.
     */
    @Bean
    @ConditionalOnMissingBean(name = "metricsCommonTags")
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> {
            registry.config()
                    .commonTags("application", properties.getApplicationName());

            // Add custom common tags
            Map<String, String> customTags = properties.getCommonTags().getTags();
            if (!customTags.isEmpty()) {
                var tags = customTags.entrySet().stream()
                        .map(e -> Tag.of(e.getKey(), e.getValue()))
                        .collect(Collectors.toList());
                registry.config()
                        .commonTags(tags);
                log.debug("Added custom common tags: {}", customTags);
            }
        };
    }
}
