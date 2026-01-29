package com.nexora.security.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Security properties for encryption, JWT, and SMS login.
 *
 * <p>Configuration example:
 * <pre>
 * nexora.security.jasypt.enabled=true
 * nexora.security.jasypt.password=${JASYPT_PASSWORD}
 * nexora.security.jwt.secret=${JWT_SECRET}
 * nexora.security.jwt.expiration=1h
 * nexora.security.sms.enabled=true
 * nexora.security.sms.code-expiration=5m
 * </pre>
 *
 * @author sujie
 */
@Data
@ConfigurationProperties(prefix = "nexora.security")
public class SecurityProperties {

    /**
     * Jasypt encryption configuration.
     */
    private Jasypt jasypt = new Jasypt();

    /**
     * JWT configuration.
     */
    private Jwt jwt = new Jwt();

    /**
     * SMS login configuration.
     */
    private Sms sms = new Sms();

    @Data
    public static class Jasypt {
        /**
         * Enable Jasypt encryption.
         */
        private boolean enabled = false;

        /**
         * Encryption password (should be externalized).
         */
        private String password;

        /**
         * Encryption algorithm.
         */
        private String algorithm = "PBEWITHHMACSHA512ANDAES_256";

        /**
         * Key obtention iterations.
         */
        private int keyObtentionIterations = 1000;

        /**
         * Pool size.
         */
        private int poolSize = 1;

        /**
         * Salt generator class name.
         */
        private String saltGeneratorClassname = "org.jasypt.salt.RandomSaltGenerator";

        /**
         * IV generator class name.
         */
        private String ivGeneratorClassname = "org.jasypt.iv.RandomIvGenerator";
    }

    @Data
    public static class Jwt {
        /**
         * Enable JWT support.
         */
        private boolean enabled = false;

        /**
         * JWT secret key (should be at least 256 bits).
         */
        private String secret;

        /**
         * Token expiration time.
         */
        private Duration expiration = Duration.ofHours(1);

        /**
         * Refresh token expiration time.
         */
        private Duration refreshExpiration = Duration.ofDays(7);

        /**
         * Token issuer.
         */
        private String issuer = "nexora-auth";

        /**
         * Token audience.
         */
        private String audience = "nexora-api";
    }

    @Data
    public static class Sms {
        /**
         * Enable SMS login support.
         */
        private boolean enabled = false;

        /**
         * SMS code expiration time.
         */
        private Duration codeExpiration = Duration.ofMinutes(5);

        /**
         * Maximum number of SMS send attempts per phone number.
         */
        private int maxSendAttempts = 10;

        /**
         * Length of the generated SMS verification code.
         */
        private int codeLength = 6;

        /**
         * Whether captcha is required before sending SMS.
         */
        private boolean requireCaptcha = true;

        /**
         * Login processing URL for SMS authentication.
         */
        private String loginProcessingUrl = "/auth/sms/login";
    }
}
