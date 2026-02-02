package com.nexora.security.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.salt.RandomSaltGenerator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Jasypt encryption auto-configuration.
 *
 * <p>Provides encryption for sensitive properties in configuration files.
 *
 * <p>Usage:
 * <pre>
 * # Encrypted value (use ENC() wrapper)
 * db.password=ENC(encrypted_value_here)
 * </pre>
 *
 * @author sujie
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(StandardPBEStringEncryptor.class)
@EnableConfigurationProperties(SecurityProperties.class)
@ConditionalOnProperty(prefix = "nexora.security.jasypt", name = "enabled", havingValue = "true")
public class JasyptAutoConfiguration {

    private final SecurityProperties.Jasypt jasypt;

    public JasyptAutoConfiguration(SecurityProperties properties) {
        this.jasypt = properties.getJasypt();
    }

    @Bean
    @ConditionalOnMissingBean
    public StandardPBEStringEncryptor stringEncryptor() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();

        encryptor.setAlgorithm(jasypt.getAlgorithm());
        encryptor.setPassword(jasypt.getPassword());
        encryptor.setKeyObtentionIterations(jasypt.getKeyObtentionIterations());
        encryptor.setSaltGenerator(new RandomSaltGenerator());

        log.info("Initialized Jasypt encryptor with algorithm: {}", jasypt.getAlgorithm());

        return encryptor;
    }
}
