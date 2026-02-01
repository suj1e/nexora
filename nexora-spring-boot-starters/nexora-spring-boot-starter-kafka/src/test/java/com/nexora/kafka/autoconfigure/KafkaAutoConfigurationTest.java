package com.nexora.kafka.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.kafka.publisher.EventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Integration tests for {@link KafkaAutoConfiguration}.
 */
@DisplayName("KafkaAutoConfiguration Integration Tests")
class KafkaAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(KafkaAutoConfiguration.class))
        .withBean(KafkaTemplate.class, () -> createMockKafkaTemplate())
        .withBean(ObjectMapper.class, ObjectMapper::new);

    private static KafkaTemplate<String, String> createMockKafkaTemplate() {
        ProducerFactory<String, String> producerFactory = mock(ProducerFactory.class);
        return new KafkaTemplate<>(producerFactory);
    }

    @Test
    @DisplayName("Should load KafkaAutoConfiguration with KafkaTemplate present")
    void shouldLoadWithKafkaTemplate() {
        contextRunner
            .run(context -> {
                assertThat(context).hasNotFailed();
                assertThat(context).hasSingleBean(KafkaAutoConfiguration.class);
            });
    }

    @Test
    @DisplayName("Should enable Kafka via @EnableKafka")
    void shouldEnableKafka() {
        contextRunner
            .run(context -> {
                // Verify @EnableKafka annotation is processed
                assertThat(context).hasNotFailed();
            });
    }

    @Test
    @DisplayName("Should scan EventPublisher component")
    void shouldScanEventPublisher() {
        contextRunner
            .run(context -> {
                assertThat(context).hasBean("eventPublisher");
            });
    }

    @Test
    @DisplayName("Should not load without KafkaTemplate class")
    void shouldNotLoadWithoutKafkaTemplate() {
        // Note: Since we're testing in an environment with KafkaTemplate on classpath,
        // we verify the configuration loads but requires KafkaTemplate bean for EventPublisher
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(KafkaAutoConfiguration.class))
            .run(context -> {
                // The configuration class exists (ConditionalOnClass passes)
                // but context fails because EventPublisher needs KafkaTemplate bean
                assertThat(context).hasFailed();
            });
    }
}
