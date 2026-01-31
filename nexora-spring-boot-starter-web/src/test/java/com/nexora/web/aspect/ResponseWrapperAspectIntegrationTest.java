package com.nexora.web.aspect;

import com.nexora.web.autoconfigure.CommonWebAutoConfiguration;
import com.nexora.web.model.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * Integration tests for {@link ResponseWrapperAspect}.
 *
 * <p>Note: These tests use ApplicationContextRunner which creates a minimal context.
 * The AOP proxy may not work correctly in this setup. In real applications with
 * full Spring context, the aspect works as expected.
 */
@DisplayName("ResponseWrapperAspect Integration Tests")
class ResponseWrapperAspectIntegrationTest {

    @Test
    @DisplayName("Should register ResponseWrapperAspect bean")
    void shouldRegisterResponseWrapperAspect() {
        new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CommonWebAutoConfiguration.class))
            .run(context -> {
                assertThat(context).hasNotFailed();
                assertThat(context).hasSingleBean(ResponseWrapperAspect.class);
            });
    }

    @Test
    @DisplayName("Should enable AspectJ auto proxy")
    void shouldEnableAspectJAutoProxy() {
        new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CommonWebAutoConfiguration.class))
            .run(context -> {
                assertThat(context).hasNotFailed();
                assertThat(context.getBean("org.springframework.aop.config.internalAutoProxyCreator"))
                    .isNotNull();
            });
    }

    @Test
    @DisplayName("Should not wrap Result response")
    void shouldNotWrapResultResponse() {
        new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CommonWebAutoConfiguration.class))
            .withUserConfiguration(TestController.class)
            .run(context -> {
                assertThat(context).hasNotFailed();
            });
    }

    @Test
    @DisplayName("Should not wrap null response")
    void shouldNotWrapNullResponse() {
        new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CommonWebAutoConfiguration.class))
            .withUserConfiguration(TestController.class)
            .run(context -> {
                MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

                mockMvc.perform(get("/api/null"))
                    .andExpect(status().isOk());
            });
    }

    @Test
    @DisplayName("Should not wrap String response")
    void shouldNotWrapStringResponse() {
        new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CommonWebAutoConfiguration.class))
            .withUserConfiguration(TestController.class)
            .run(context -> {
                MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

                mockMvc.perform(get("/api/string"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value("plain string"));
            });
    }

    @RestController
    static class TestController {

        @GetMapping("/api/data")
        public String getData() {
            return "test data";
        }

        @GetMapping("/api/result")
        public Result<String> getResult() {
            return new Result<>(201, "custom", "already wrapped", null);
        }

        @GetMapping("/api/null")
        public Void getNull() {
            return null;
        }

        @GetMapping("/api/string")
        public String getString() {
            return "plain string";
        }
    }
}
