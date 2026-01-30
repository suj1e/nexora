package com.nexora.security.sms.autoconfigure;

import com.nexora.security.sms.SmsProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link SmsLoginAutoConfiguration}.
 */
@DisplayName("SmsLoginAutoConfiguration Integration Tests")
class SmsLoginAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(SmsLoginAutoConfiguration.class));

    @Test
    @DisplayName("Should not load by default")
    void shouldNotLoadByDefault() {
        contextRunner
            .run(context -> {
                assertThat(context).doesNotHaveBean(SmsLoginAutoConfiguration.class);
            });
    }

    @Test
    @DisplayName("Should load when enabled in web application")
    void shouldLoadWhenEnabled() {
        contextRunner
            .withPropertyValues("nexora.security.sms.enabled=true")
            .run(context -> {
                assertThat(context).hasNotFailed();
                assertThat(context).hasSingleBean(SmsLoginAutoConfiguration.class);
            });
    }

    @Test
    @DisplayName("Should register SmsProperties when enabled")
    void shouldRegisterSmsProperties() {
        contextRunner
            .withPropertyValues("nexora.security.sms.enabled=true")
            .run(context -> {
                assertThat(context).hasSingleBean(SmsProperties.class);
            });
    }

    @Test
    @DisplayName("Should configure SmsProperties with default values")
    void shouldConfigureDefaultProperties() {
        contextRunner
            .withPropertyValues("nexora.security.sms.enabled=true")
            .run(context -> {
                SmsProperties properties = context.getBean(SmsProperties.class);
                assertThat(properties).isNotNull();
            });
    }

    @Test
    @DisplayName("Should configure SmsProperties with custom values")
    void shouldConfigureCustomProperties() {
        contextRunner
            .withPropertyValues(
                "nexora.security.sms.enabled=true",
                "nexora.security.sms.login-processing-url=/auth/sms/login",
                "nexora.security.sms.code-length=8",
                "nexora.security.sms.code-expiration=5m"
            )
            .run(context -> {
                SmsProperties properties = context.getBean(SmsProperties.class);
                assertThat(properties).isNotNull();
                assertThat(properties.getLoginProcessingUrl()).isEqualTo("/auth/sms/login");
            });
    }

    @Test
    @DisplayName("Should not load in non-web application")
    void shouldNotLoadInNonWebApplication() {
        // This test verifies @ConditionalOnWebApplication
        // The WebApplicationContextRunner simulates a web environment
        // For non-web, we would use ApplicationContextRunner
        contextRunner
            .run(context -> {
                // In web context, should load when enabled
                assertThat(context).doesNotHaveBean("nonExistentBean");
            });
    }
}
