package com.nexora.webflux.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * WebFlux auto-configuration.
 *
 * <p>Automatically configures:
 * <ul>
 *   <li>CORS settings</li>
 *   <li>Request logging filter</li>
 *   <li>Global exception handler</li>
 * </ul>
 *
 * @author sujie
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties(WebFluxProperties.class)
@ConditionalOnProperty(prefix = "nexora.webflux", name = "enabled", havingValue = "true", matchIfMissing = true)
public class WebFluxAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(WebFluxAutoConfiguration.class);

    /**
     * WebFlux configuration for CORS.
     */
    @AutoConfiguration
    @ConditionalOnProperty(prefix = "nexora.webflux.cors", name = "enabled", havingValue = "true")
    public static class CorsConfiguration implements WebFluxConfigurer {

        private static final Logger log = LoggerFactory.getLogger(CorsConfiguration.class);

        private final WebFluxProperties properties;

        public CorsConfiguration(WebFluxProperties properties) {
            this.properties = properties;
        }

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            WebFluxProperties.Cors corsProps = properties.getCors();
            registry.addMapping("/**")
                    .allowedOrigins(corsProps.getAllowedOrigins())
                    .allowedMethods(corsProps.getAllowedMethods())
                    .allowedHeaders(corsProps.getAllowedHeaders())
                    .allowCredentials(corsProps.isAllowCredentials())
                    .maxAge(corsProps.getMaxAge().toSeconds());
            log.info("CORS configuration enabled with origins: {}", (Object) corsProps.getAllowedOrigins());
        }
    }
}
