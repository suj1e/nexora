package com.nexora.security.autoconfigure;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link JasyptAutoConfiguration}.
 */
@DisplayName("JasyptAutoConfiguration Integration Tests")
class JasyptAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(JasyptAutoConfiguration.class));

    @Test
    @DisplayName("Should not load by default")
    void shouldNotLoadByDefault() {
        contextRunner
            .run(context -> {
                assertThat(context).doesNotHaveBean("stringEncryptor");
            });
    }

    @Test
    @DisplayName("Should load when enabled")
    void shouldLoadWhenEnabled() {
        contextRunner
            .withPropertyValues(
                "nexora.security.jasypt.enabled=true",
                "nexora.security.jasypt.password=test-password"
            )
            .run(context -> {
                assertThat(context).hasNotFailed();
                assertThat(context).hasSingleBean(StandardPBEStringEncryptor.class);
            });
    }

    @Test
    @DisplayName("Should create stringEncryptor bean")
    void shouldCreateStringEncryptor() {
        contextRunner
            .withPropertyValues(
                "nexora.security.jasypt.enabled=true",
                "nexora.security.jasypt.password=test-password"
            )
            .run(context -> {
                assertThat(context).hasBean("stringEncryptor");
                StandardPBEStringEncryptor encryptor = context.getBean(StandardPBEStringEncryptor.class);
                assertThat(encryptor).isNotNull();
            });
    }

    @Test
    @DisplayName("Should configure encryptor with custom algorithm")
    void shouldConfigureWithCustomAlgorithm() {
        contextRunner
            .withPropertyValues(
                "nexora.security.jasypt.enabled=true",
                "nexora.security.jasypt.password=test-password",
                "nexora.security.jasypt.algorithm=PBEWITHMD5ANDDES"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(StandardPBEStringEncryptor.class);
            });
    }

    @Test
    @DisplayName("Should configure encryptor with custom iterations")
    void shouldConfigureWithCustomIterations() {
        contextRunner
            .withPropertyValues(
                "nexora.security.jasypt.enabled=true",
                "nexora.security.jasypt.password=test-password",
                "nexora.security.jasypt.key-obtention-iterations=5000"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(StandardPBEStringEncryptor.class);
            });
    }

    @Test
    @DisplayName("Should register SecurityProperties")
    void shouldRegisterSecurityProperties() {
        contextRunner
            .withPropertyValues(
                "nexora.security.jasypt.enabled=true",
                "nexora.security.jasypt.password=test-password"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(SecurityProperties.class);
            });
    }

    @Test
    @DisplayName("Should not load without StandardPBEStringEncryptor class")
    void shouldNotLoadWithoutEncryptorClass() {
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JasyptAutoConfiguration.class))
            .run(context -> {
                assertThat(context).doesNotHaveBean(JasyptAutoConfiguration.class);
            });
    }
}
