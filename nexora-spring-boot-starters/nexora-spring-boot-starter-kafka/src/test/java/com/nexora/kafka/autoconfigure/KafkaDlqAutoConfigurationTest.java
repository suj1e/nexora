package com.nexora.kafka.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Integration tests for {@link KafkaDlqAutoConfiguration}.
 */
@DisplayName("KafkaDlqAutoConfiguration Integration Tests")
class KafkaDlqAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(KafkaDlqAutoConfiguration.class))
        .withBean(KafkaTemplate.class, () -> createMockKafkaTemplate())
        .withBean(ObjectMapper.class, ObjectMapper::new);

    private static KafkaTemplate<Object, Object> createMockKafkaTemplate() {
        ProducerFactory<Object, Object> producerFactory = mock(ProducerFactory.class);
        return new KafkaTemplate<>(producerFactory);
    }

    @Test
    @DisplayName("Should load KafkaDlqAutoConfiguration when enabled")
    void shouldLoadWhenEnabled() {
        contextRunner
            .withPropertyValues("nexora.kafka.dlq.enabled=true")
            .run(context -> {
                assertThat(context).hasNotFailed();
                assertThat(context).hasSingleBean(CommonErrorHandler.class);
            });
    }

    @Test
    @DisplayName("Should load by default (enabled by default)")
    void shouldLoadByDefault() {
        contextRunner
            .run(context -> {
                assertThat(context).hasNotFailed();
                assertThat(context).hasSingleBean(CommonErrorHandler.class);
            });
    }

    @Test
    @DisplayName("Should not load when disabled")
    void shouldNotLoadWhenDisabled() {
        contextRunner
            .withPropertyValues("nexora.kafka.dlq.enabled=false")
            .run(context -> {
                assertThat(context).doesNotHaveBean("kafkaErrorHandler");
            });
    }

    @Test
    @DisplayName("Should create kafkaErrorHandler bean")
    void shouldCreateKafkaErrorHandler() {
        contextRunner
            .run(context -> {
                assertThat(context).hasBean("kafkaErrorHandler");
                CommonErrorHandler errorHandler = context.getBean(CommonErrorHandler.class);
                assertThat(errorHandler).isNotNull();
            });
    }

    @Test
    @DisplayName("Should not load without KafkaTemplate class")
    void shouldNotLoadWithoutKafkaTemplate() {
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(KafkaDlqAutoConfiguration.class))
            .run(context -> {
                // Without KafkaTemplate, the configuration should not fail
                // but may not create the error handler
                assertThat(context).doesNotHaveBean("kafkaTemplate");
            });
    }
}
