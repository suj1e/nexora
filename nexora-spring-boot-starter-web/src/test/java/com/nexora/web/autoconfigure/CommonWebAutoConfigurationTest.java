package com.nexora.web.autoconfigure;

import com.nexora.web.aspect.ResponseWrapperAspect;
import com.nexora.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.ApplicationContext;

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
    @DisplayName("Should register ResponseWrapperAspect bean")
    void shouldRegisterResponseWrapperAspect() {
        contextRunner
            .run(context -> {
                assertThat(context).hasSingleBean(ResponseWrapperAspect.class);
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

    @Test
    @DisplayName("Should enable AspectJ auto proxy")
    void shouldEnableAspectJAutoProxy() {
        contextRunner
            .run(context -> {
                assertThat(context.getBean("org.springframework.aop.config.internalAutoProxyCreator"))
                    .isNotNull();
            });
    }
}
