package com.nexora.web.exception;

import com.nexora.web.autoconfigure.CommonWebAutoConfiguration;
import com.nexora.web.model.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link GlobalExceptionHandler}.
 */
@DisplayName("GlobalExceptionHandler Integration Tests")
class GlobalExceptionHandlerIntegrationTest {

    @Test
    @DisplayName("Should handle BusinessException with 400 status")
    void shouldHandleBusinessException() {
        new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CommonWebAutoConfiguration.class))
            .withUserConfiguration(TestController.class)
            .run(context -> {
                MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

                mockMvc.perform(get("/api/business-error"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Business error occurred"));
            });
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException with 400 status")
    void shouldHandleIllegalArgumentException() {
        new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CommonWebAutoConfiguration.class))
            .withUserConfiguration(TestController.class)
            .run(context -> {
                MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

                mockMvc.perform(get("/api/illegal-argument"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Invalid argument provided"));
            });
    }

    @Test
    @DisplayName("Should handle IllegalStateException with 400 status")
    void shouldHandleIllegalStateException() {
        new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CommonWebAutoConfiguration.class))
            .withUserConfiguration(TestController.class)
            .run(context -> {
                MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

                mockMvc.perform(get("/api/illegal-state"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Invalid state"));
            });
    }

    @Test
    @DisplayName("Should handle generic Exception with 500 status")
    void shouldHandleGenericException() {
        new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CommonWebAutoConfiguration.class))
            .withUserConfiguration(TestController.class)
            .run(context -> {
                MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

                mockMvc.perform(get("/api/server-error"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("Internal server error"));
            });
    }

    @Test
    @DisplayName("Should handle validation error with 400 status")
    void shouldHandleValidationError() {
        new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CommonWebAutoConfiguration.class))
            .withUserConfiguration(TestController.class)
            .run(context -> {
                MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

                mockMvc.perform(get("/api/validated/null"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
            });
    }

    @RestController
    @Validated
    static class TestController {

        @GetMapping("/api/business-error")
        public String businessError() {
            throw new BusinessException("Business error occurred");
        }

        @GetMapping("/api/business-error-with-code")
        public String businessErrorWithCode() {
            throw new BusinessException(404, "Resource not found");
        }

        @GetMapping("/api/illegal-argument")
        public String illegalArgument() {
            throw new IllegalArgumentException("Invalid argument provided");
        }

        @GetMapping("/api/illegal-state")
        public String illegalState() {
            throw new IllegalStateException("Invalid state");
        }

        @GetMapping("/api/server-error")
        public String serverError() {
            throw new RuntimeException("Unexpected error");
        }

        @GetMapping("/api/validated/null")
        public String validatedNull(@NotNull(message = "Path variable cannot be null") @PathVariable String id) {
            return id;
        }
    }
}
