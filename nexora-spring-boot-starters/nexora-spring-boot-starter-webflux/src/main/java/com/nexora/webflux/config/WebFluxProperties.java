package com.nexora.webflux.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * WebFlux configuration properties.
 *
 * @author sujie
 */
@ConfigurationProperties(prefix = "nexora.webflux")
public class WebFluxProperties {

    /**
     * Enable WebFlux auto-configuration.
     */
    private boolean enabled = true;

    /**
     * Global CORS configuration.
     */
    private Cors cors = new Cors();

    /**
     * Request timeout.
     */
    private Duration requestTimeout = Duration.ofSeconds(30);

    /**
     * Global request logging.
     */
    private Logging logging = new Logging();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Cors getCors() {
        return cors;
    }

    public void setCors(Cors cors) {
        this.cors = cors;
    }

    public Duration getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Duration requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public Logging getLogging() {
        return logging;
    }

    public void setLogging(Logging logging) {
        this.logging = logging;
    }

    public static class Cors {
        /**
         * Enable CORS.
         */
        private boolean enabled = false;

        /**
         * Allowed origins.
         */
        private String[] allowedOrigins = {"*"};

        /**
         * Allowed methods.
         */
        private String[] allowedMethods = {"GET", "POST", "PUT", "DELETE", "OPTIONS"};

        /**
         * Allowed headers.
         */
        private String[] allowedHeaders = {"*"};

        /**
         * Allow credentials.
         */
        private boolean allowCredentials = true;

        /**
         * Max age for preflight requests.
         */
        private Duration maxAge = Duration.ofHours(1);

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String[] getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(String[] allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }

        public String[] getAllowedMethods() {
            return allowedMethods;
        }

        public void setAllowedMethods(String[] allowedMethods) {
            this.allowedMethods = allowedMethods;
        }

        public String[] getAllowedHeaders() {
            return allowedHeaders;
        }

        public void setAllowedHeaders(String[] allowedHeaders) {
            this.allowedHeaders = allowedHeaders;
        }

        public boolean isAllowCredentials() {
            return allowCredentials;
        }

        public void setAllowCredentials(boolean allowCredentials) {
            this.allowCredentials = allowCredentials;
        }

        public Duration getMaxAge() {
            return maxAge;
        }

        public void setMaxAge(Duration maxAge) {
            this.maxAge = maxAge;
        }
    }

    public static class Logging {
        /**
         * Enable request logging.
         */
        private boolean enabled = true;

        /**
         * Log request body.
         */
        private boolean logBody = false;

        /**
         * Log response body.
         */
        private boolean logResponseBody = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isLogBody() {
            return logBody;
        }

        public void setLogBody(boolean logBody) {
            this.logBody = logBody;
        }

        public boolean isLogResponseBody() {
            return logResponseBody;
        }

        public void setLogResponseBody(boolean logResponseBody) {
            this.logResponseBody = logResponseBody;
        }
    }
}
