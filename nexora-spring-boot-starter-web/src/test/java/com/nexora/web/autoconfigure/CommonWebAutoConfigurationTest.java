package com.nexora.web.autoconfigure;

import com.nexora.common.api.Result;
import com.nexora.common.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link CommonWebAutoConfiguration}.
 */
@DisplayName("CommonWebAutoConfiguration Integration Tests")
class CommonWebAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(CommonWebAutoConfiguration.class));

    @Test
    @DisplayName("Should load CommonWebAutoConfiguration in web application")
    void shouldLoadAutoConfiguration() {
        contextRunner
            .run(context -> {
                assertThat(context).hasNotFailed();
                assertThat(context).hasSingleBean(CommonWebAutoConfiguration.class);
            });
    }

    @Test
    @DisplayName("Should register GlobalExceptionHandler bean")
    void shouldRegisterGlobalExceptionHandler() {
        contextRunner
            .run(context -> {
                assertThat(context).hasSingleBean(GlobalExceptionHandler.class);
            });
    }
}
