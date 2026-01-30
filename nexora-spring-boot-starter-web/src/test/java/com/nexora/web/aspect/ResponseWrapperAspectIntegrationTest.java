package com.nexora.web.aspect;

import com.nexora.web.autoconfigure.CommonWebAutoConfiguration;
import com.nexora.web.model.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link ResponseWrapperAspect}.
 */
@DisplayName("ResponseWrapperAspect Integration Tests")
class ResponseWrapperAspectIntegrationTest {

    @Test
    @DisplayName("Should wrap controller response in Result")
    void shouldWrapControllerResponse() {
        new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CommonWebAutoConfiguration.class))
            .withUserConfiguration(TestController.class)
            .run(context -> {
                MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

                mockMvc.perform(get("/api/data"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("success"))
                    .andExpect(jsonPath("$.data").value("test data"));
            });
    }

    @Test
    @DisplayName("Should not wrap Result response")
    void shouldNotWrapResultResponse() {
        new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CommonWebAutoConfiguration.class))
            .withUserConfiguration(TestController.class)
            .run(context -> {
                MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

                mockMvc.perform(get("/api/result"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(201))
                    .andExpect(jsonPath("$.message").value("custom"));
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
            return Result.<String>builder()
                .code(201)
                .message("custom")
                .data("already wrapped")
                .build();
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
