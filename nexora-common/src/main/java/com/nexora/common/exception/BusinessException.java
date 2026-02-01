package com.nexora.common.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * Business exception base class.
 *
 * <p>Use this for business logic errors that should return 4xx status codes.
 *
 * <p>Usage:
 * <pre>
 * // Simple error
 * throw new BusinessException("User not found");
 *
 * // With error code enum
 * throw new BusinessException(ErrorCode.USER_NOT_FOUND);
 *
 * // With int code
 * throw new BusinessException(404, "Resource not found");
 *
 * // With context
 * throw new BusinessException(ErrorCode.USER_NOT_FOUND)
 *     .withContext("userId", userId);
 * </pre>
 *
 * @author sujie
 */
public class BusinessException extends RuntimeException {

    private final int code;
    private final ErrorCode errorCode;
    private final String traceId;
    private final Map<String, Object> context;

    /**
     * Error code enum for standardized business errors.
     */
    public enum ErrorCode {
        // Common errors (4xx)
        BAD_REQUEST(400, "Bad Request"),
        UNAUTHORIZED(401, "Unauthorized"),
        FORBIDDEN(403, "Forbidden"),
        NOT_FOUND(404, "Resource Not Found"),
        CONFLICT(409, "Resource Conflict"),
        VALIDATION_FAILED(422, "Validation Failed"),

        // User errors (4000-4099)
        USER_NOT_FOUND(4001, "User not found"),
        USER_ALREADY_EXISTS(4002, "User already exists"),
        USER_DISABLED(4003, "User account is disabled"),
        INVALID_CREDENTIALS(4004, "Invalid username or password"),

        // Resource errors (4100-4199)
        RESOURCE_NOT_FOUND(4101, "Resource not found"),
        RESOURCE_ALREADY_EXISTS(4102, "Resource already exists"),
        RESOURCE_LOCKED(4103, "Resource is locked"),
        RESOURCE_EXPIRED(4104, "Resource has expired"),

        // Permission errors (4200-4299)
        PERMISSION_DENIED(4201, "Permission denied"),
        INSUFFICIENT_PRIVILEGES(4202, "Insufficient privileges"),
        ACCESS_DENIED(4203, "Access denied"),

        // Operation errors (4300-4399)
        OPERATION_FAILED(4301, "Operation failed"),
        OPERATION_NOT_SUPPORTED(4302, "Operation not supported"),
        OPERATION_TIMEOUT(4303, "Operation timeout"),

        // State errors (4400-4499)
        INVALID_STATE(4401, "Invalid state"),
        DUPLICATE_OPERATION(4402, "Duplicate operation"),

        // External service errors (4500-4599)
        EXTERNAL_SERVICE_ERROR(4501, "External service error"),
        EXTERNAL_SERVICE_TIMEOUT(4502, "External service timeout");

        private final int code;
        private final String defaultMessage;

        ErrorCode(int code, String defaultMessage) {
            this.code = code;
            this.defaultMessage = defaultMessage;
        }

        public int getCode() {
            return code;
        }

        public String getDefaultMessage() {
            return defaultMessage;
        }
    }

    /**
     * Create exception with message (default 400 code).
     */
    public BusinessException(String message) {
        this(ErrorCode.BAD_REQUEST.getCode(), message, null, null);
    }

    /**
     * Create exception with error code enum.
     */
    public BusinessException(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getDefaultMessage(), errorCode, null);
    }

    /**
     * Create exception with error code enum and custom message.
     */
    public BusinessException(ErrorCode errorCode, String message) {
        this(errorCode.getCode(), message, errorCode, null);
    }

    /**
     * Create exception with code and message.
     */
    public BusinessException(int code, String message) {
        this(code, message, null, null);
    }

    /**
     * Create exception with code, message, and cause.
     */
    public BusinessException(int code, String message, Throwable cause) {
        this(code, message, null, cause);
    }

    /**
     * Internal constructor with all parameters.
     */
    private BusinessException(int code, String message, ErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.errorCode = errorCode;
        this.traceId = generateTraceId();
        this.context = new HashMap<>();
    }

    /**
     * Get error code.
     */
    public int getCode() {
        return code;
    }

    /**
     * Get error code enum.
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * Get trace ID.
     */
    public String getTraceId() {
        return traceId;
    }

    /**
     * Get context metadata.
     */
    public Map<String, Object> getContext() {
        return new HashMap<>(context);
    }

    /**
     * Get context value by key.
     */
    public Object getContextValue(String key) {
        return context.get(key);
    }

    /**
     * Add context to this exception (for chaining).
     * <pre>
     * throw new BusinessException(ErrorCode.USER_NOT_FOUND)
     *     .withContext("userId", userId)
     *     .withContext("email", email);
     * </pre>
     */
    public BusinessException withContext(String key, Object value) {
        this.context.put(key, value);
        return this;
    }

    /**
     * Create a new BusinessException builder pattern.
     */
    public static BusinessException of(ErrorCode errorCode) {
        return new BusinessException(errorCode);
    }

    /**
     * Generate trace ID.
     */
    private static String generateTraceId() {
        return java.util.UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        String message = String.format("BusinessException[code=%d, message='%s'", code, getMessage());
        if (traceId != null && !traceId.isEmpty()) {
            message += ", traceId='" + traceId + "'";
        }
        if (!context.isEmpty()) {
            message += ", context=" + context;
        }
        return message + "]";
    }
}
