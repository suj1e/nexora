package com.nexora.security.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * SMS login configuration properties.
 *
 * <p>Configuration example:
 * <pre>
 * nexora:
 *   security:
 *     sms:
 *       enabled: true
 *       code-expiration: 5m
 *       max-send-attempts: 10
 *       code-length: 6
 *       require-captcha: true
 *       login-processing-url: /auth/sms/login
 * </pre>
 *
 * @author sujie
 */
@Data
@ConfigurationProperties(prefix = "nexora.security.sms")
public class SmsProperties {

    /**
     * Enable SMS login feature.
     */
    private boolean enabled = false;

    /**
     * SMS code expiration time.
     * <p>Used by the application to set TTL for stored codes.
     */
    private Duration codeExpiration = Duration.ofMinutes(5);

    /**
     * Maximum number of SMS send attempts per phone number.
     * <p>Used by the application to prevent abuse.
     */
    private int maxSendAttempts = 10;

    /**
     * Length of the generated SMS verification code.
     * <p>Used by the application to generate codes.
     */
    private int codeLength = 6;

    /**
     * Whether captcha is required before sending SMS.
     * <p>Used by the application to prevent automated abuse.
     */
    private boolean requireCaptcha = true;

    /**
     * Login processing URL for SMS authentication.
     * <p>Default: /auth/sms/login
     */
    private String loginProcessingUrl = "/auth/sms/login";
}
