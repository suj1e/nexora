package com.nexora.kafka.autoconfigure;

import com.nexora.kafka.properties.KafkaProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Kafka DLQ (Dead Letter Queue) auto-configuration.
 *
 * <p>When message consumption fails, the message is sent to a DLQ topic
 * for later processing or manual intervention.
 *
 * <p>DLQ topic naming: {original-topic}.dlq
 *
 * @author sujie
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.kafka.core.KafkaTemplate")
@ConditionalOnProperty(prefix = "nexora.kafka.dlq", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaDlqAutoConfiguration {

    @Autowired(required = false)
    private KafkaTemplate<Object, Object> kafkaTemplate;

    private final KafkaProperties properties;

    public KafkaDlqAutoConfiguration(KafkaProperties properties) {
        this.properties = properties;
    }

    /**
     * Common error handler with DLQ support.
     *
     * <p>Configuration:
     * <ul>
     *   <li>Max retry attempts: configurable via nexora.kafka.dlq.retry-attempts (default: 3)</li>
     *   <li>Backoff interval: configurable via nexora.kafka.dlq.retry-interval-ms (default: 1000ms)</li>
     *   <li>Failed messages sent to DLQ topic</li>
     * </ul>
     */
    @Bean
    public CommonErrorHandler kafkaErrorHandler() {
        if (kafkaTemplate == null) {
            log.warn("KafkaTemplate not available, DLQ disabled");
            // Return a simple error handler without DLQ
            return new DefaultErrorHandler();
        }

        // Fixed backoff: configurable retry interval and max attempts
        FixedBackOff backOff = new FixedBackOff(
            properties.getDlq().getRetryIntervalMs(),
            properties.getDlq().getRetryAttempts()
        );

        // Dead letter publishing recoverer
        // Automatically sends failed messages to DLQ topic
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
            kafkaTemplate,
            (record, exception) -> {
                // DLQ topic name: original-topic + ".dlq"
                String dlqTopic = record.topic() + ".dlq";
                return new org.apache.kafka.common.TopicPartition(dlqTopic, record.partition());
            }
        );

        // Default error handler with DLQ
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
            recoverer,
            backOff
        );

        // Configure which exceptions should NOT be retried
        errorHandler.addNotRetryableExceptions(
            IllegalArgumentException.class,
            org.springframework.kafka.support.serializer.DeserializationException.class
        );

        log.info("Kafka DLQ error handler initialized with retryAttempts={}, retryIntervalMs={}",
            properties.getDlq().getRetryAttempts(), properties.getDlq().getRetryIntervalMs());

        return errorHandler;
    }
}
