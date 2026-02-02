package com.nexora.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexora.common.exception.BusinessException;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * RFC 7807 Problem Detail for HTTP APIs.
 *
 * <p>This class represents a standardized error response format as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc7807">RFC 77807</a>.
 *
 * <p>Example response:
 * <pre>
 * {
 *   "type": "https://api.nexora.com/errors/business/not-found",
 *   "title": "Resource Not Found",
 *   "status": 404,
 *   "detail": "User with id '123' not found",
 *   "instance": "/api/users/123",
 *   "timestamp": "2024-01-28T10:00:00Z",
 *   "traceId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
 *   "errorCode": "USER_NOT_FOUND",
 *   "context": {
 *     "userId": "123"
 *   }
 * }
 * </pre>
 *
 * @author sujie
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProblemDetail(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        Instant timestamp,
        String traceId,
        String errorCode,
        Map<String, Object> context
) {

    /**
     * Problem detail builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Create problem detail from BusinessException.
     */
    public static ProblemDetail fromBusinessException(
            BusinessException ex,
            String instance,
            int status) {
        return builder()
                .type("https://api.nexora.com/errors/business")
                .title(ex.getErrorCode() != null ? ex.getErrorCode().getDefaultMessage() : "Business Error")
                .status(status)
                .detail(ex.getMessage())
                .instance(instance)
                .errorCode(ex.getErrorCode() != null ? ex.getErrorCode().name() : null)
                .traceId(ex.getTraceId())
                .context(ex.getContext())
                .build();
    }

    /**
     * Create problem detail for validation errors.
     */
    public static ProblemDetail validationError(
            String detail,
            String instance,
            Map<String, Object> errors) {
        return builder()
                .type("https://api.nexora.com/errors/validation")
                .title("Validation Failed")
                .status(422)
                .detail(detail)
                .instance(instance)
                .context(errors)
                .build();
    }

    /**
     * Create problem detail for resource not found.
     */
    public static ProblemDetail notFound(
            String resourceType,
            String resourceId,
            String instance) {
        return builder()
                .type("https://api.nexora.com/errors/not-found")
                .title("Resource Not Found")
                .status(404)
                .detail(String.format("%s with id '%s' not found", resourceType, resourceId))
                .instance(instance)
                .build();
    }

    /**
     * Create problem detail for internal server error.
     */
    public static ProblemDetail internalServerError(
            String traceId,
            String instance) {
        return builder()
                .type("https://api.nexora.com/errors/internal")
                .title("Internal Server Error")
                .status(500)
                .detail("An unexpected error occurred")
                .instance(instance)
                .traceId(traceId)
                .build();
    }

    /**
     * Builder for ProblemDetail.
     */
    public static class Builder {
        private String type = "https://api.nexora.com/errors";
        private String title;
        private int status;
        private String detail;
        private String instance;
        private Instant timestamp = Instant.now();
        private String traceId = UUID.randomUUID().toString();
        private String errorCode;
        private Map<String, Object> context;

        /**
         * Set problem type (URI).
         */
        public Builder type(String type) {
            this.type = type;
            return this;
        }

        /**
         * Set problem type (URI).
         */
        public Builder type(URI type) {
            this.type = type != null ? type.toString() : null;
            return this;
        }

        /**
         * Set problem title.
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Set HTTP status code.
         */
        public Builder status(int status) {
            this.status = status;
            return this;
        }

        /**
         * Set problem detail.
         */
        public Builder detail(String detail) {
            this.detail = detail;
            return this;
        }

        /**
         * Set request instance (URI path).
         */
        public Builder instance(String instance) {
            this.instance = instance;
            return this;
        }

        /**
         * Set request instance (URI).
         */
        public Builder instance(URI instance) {
            this.instance = instance != null ? instance.toString() : null;
            return this;
        }

        /**
         * Set error timestamp.
         */
        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        /**
         * Set trace ID.
         */
        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        /**
         * Set error code.
         */
        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        /**
         * Set context metadata.
         */
        public Builder context(Map<String, Object> context) {
            this.context = context;
            return this;
        }

        /**
         * Build ProblemDetail.
         */
        public ProblemDetail build() {
            return new ProblemDetail(
                    type,
                    title,
                    status,
                    detail,
                    instance,
                    timestamp,
                    traceId,
                    errorCode,
                    context
            );
        }
    }
}
