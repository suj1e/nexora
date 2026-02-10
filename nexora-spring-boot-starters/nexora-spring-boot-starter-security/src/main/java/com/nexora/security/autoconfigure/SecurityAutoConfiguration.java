package com.nexora.security.autoconfigure;

import com.nexora.security.crypto.Encryptor;
import com.nexora.security.domain.RefreshToken;
import com.nexora.security.jwt.JwtProperties;
import com.nexora.security.jwt.JwtTokenProvider;
import com.nexora.security.jwt.ReactiveJwtTokenProvider;
import com.nexora.security.repository.RefreshTokenRepository;
import com.nexora.security.service.RefreshTokenService;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// EntityScan not available in Spring Boot 4.x milestone - using @Configuration with @ComponentScan instead
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Security auto-configuration.
 *
 * <p>Registers JWT, encryption, and refresh token beans.
 *
 * @author sujie
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(StandardPBEStringEncryptor.class)
public class SecurityAutoConfiguration {

    /**
     * JWT Token Provider configuration.
     */
    @Configuration
    @EnableConfigurationProperties(JwtProperties.class)
    @ConditionalOnProperty(prefix = "nexora.security.jwt", name = "enabled", havingValue = "true")
    public static class JwtTokenProviderConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public JwtTokenProvider jwtTokenProvider(JwtProperties properties) {
            return new JwtTokenProvider(properties);
        }

        @Bean
        @ConditionalOnMissingBean
        public ReactiveJwtTokenProvider reactiveJwtTokenProvider(JwtProperties properties) {
            return new ReactiveJwtTokenProvider(properties);
        }
    }

    /**
     * Refresh Token configuration.
     * Requires explicit opt-in via nexora.security.refresh-token.enabled property
     * to avoid JPA initialization issues in non-JPA applications.
     */
    @Configuration
    @ConditionalOnClass(name = "org.springframework.data.jpa.repository.JpaRepository")
    @ConditionalOnProperty(prefix = "nexora.security.refresh-token", name = "enabled", havingValue = "true")
    @EnableJpaRepositories(basePackages = "com.nexora.security.repository")
    // Note: EntityScan not available in Spring Boot 4.x milestone - entities should be scanned via main application configuration
    public static class RefreshTokenConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public RefreshTokenService refreshTokenService(
                RefreshTokenRepository refreshTokenRepository,
                JwtTokenProvider jwtTokenProvider) {
            return new RefreshTokenService(refreshTokenRepository, jwtTokenProvider);
        }
    }

    /**
     * Encryptor configuration.
     */
    @Configuration
    @ConditionalOnClass(StandardPBEStringEncryptor.class)
    @ConditionalOnProperty(prefix = "nexora.security.jasypt", name = "enabled", havingValue = "true")
    public static class EncryptorConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public Encryptor encryptor(StandardPBEStringEncryptor encryptor) {
            return new Encryptor(encryptor);
        }
    }
}
