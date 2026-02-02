package com.nexora.security.autoconfigure;

import com.nexora.security.crypto.Encryptor;
import com.nexora.security.jwt.JwtProperties;
import com.nexora.security.jwt.JwtTokenProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link SecurityAutoConfiguration}.
 */
@DisplayName("SecurityAutoConfiguration Integration Tests")
class SecurityAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(SecurityAutoConfiguration.class))
        .withBean(StandardPBEStringEncryptor.class, () -> new StandardPBEStringEncryptor())
        .withPropertyValues("nexora.security.jasypt.enabled=true", "nexora.security.jasypt.password=testPassword");

    @Test
    @DisplayName("Should load SecurityAutoConfiguration")
    void shouldLoad() {
        contextRunner
            .run(context -> {
                assertThat(context).hasNotFailed();
                assertThat(context).hasSingleBean(SecurityAutoConfiguration.class);
            });
    }

    @Test
    @DisplayName("Should create Encryptor bean")
    void shouldCreateEncryptor() {
        contextRunner
            .run(context -> {
                assertThat(context).hasSingleBean(Encryptor.class);
            });
    }

    @Test
    @DisplayName("Should not create JwtTokenProvider by default")
    void shouldNotCreateJwtTokenProviderByDefault() {
        contextRunner
            .run(context -> {
                assertThat(context).doesNotHaveBean(JwtTokenProvider.class);
            });
    }

    @Test
    @DisplayName("Should create JwtTokenProvider when enabled")
    void shouldCreateJwtTokenProviderWhenEnabled() {
        contextRunner
            .withPropertyValues(
                "nexora.security.jwt.enabled=true",
                "nexora.security.jwt.secret=" + "a".repeat(64),
                "nexora.security.jwt.expiration=1h",
                "nexora.security.jwt.refresh-expiration=7d"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(JwtTokenProvider.class);
            });
    }

    @Test
    @DisplayName("Should register JwtProperties when JWT is enabled")
    void shouldRegisterJwtPropertiesWhenEnabled() {
        contextRunner
            .withPropertyValues(
                "nexora.security.jwt.enabled=true",
                "nexora.security.jwt.secret=" + "a".repeat(64)
            )
            .run(context -> {
                assertThat(context).hasSingleBean(JwtProperties.class);
            });
    }

    @Test
    @DisplayName("Should not create Encryptor without jasypt enabled property")
    void shouldNotCreateEncryptorWithoutJasyptEnabled() {
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SecurityAutoConfiguration.class, JasyptAutoConfiguration.class))
            .withBean(StandardPBEStringEncryptor.class, () -> new StandardPBEStringEncryptor())
            .run(context -> {
                // Encryptor should not be created when nexora.security.jasypt.enabled is not set
                assertThat(context).doesNotHaveBean(Encryptor.class);
            });
    }
}
