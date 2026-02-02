package com.nexora.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

/**
 * Unified API response format.
 *
 * <p>This is the standard response format for all REST APIs.
 *
 * <p>Example response:
 * <pre>
 * {
 *   "code": 200,
 *   "message": "success",
 *   "data": { ... },
 *   "timestamp": "2024-01-28T10:00:00Z",
 *   "traceId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
 *   "success": true
 * }
 * </pre>
 *
 * @param <T> data type
 * @author sujie
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Result<T>(
    Integer code,
    String message,
    T data,
    String timestamp,
    String traceId,
    Boolean success
) {

    // HTTP Status Codes
    public static final int CODE_OK = 200;
    public static final int CODE_CREATED = 201;
    public static final int CODE_ACCEPTED = 202;
    public static final int CODE_NO_CONTENT = 204;
    public static final int CODE_BAD_REQUEST = 400;
    public static final int CODE_UNAUTHORIZED = 401;
    public static final int CODE_FORBIDDEN = 403;
    public static final int CODE_NOT_FOUND = 404;
    public static final int CODE_METHOD_NOT_ALLOWED = 405;
    public static final int CODE_NOT_ACCEPTABLE = 406;
    public static final int CODE_VALIDATION_ERROR = 422;
    public static final int CODE_TOO_MANY_REQUESTS = 429;
    public static final int CODE_SERVICE_UNAVAILABLE = 503;
    public static final int CODE_INTERNAL_ERROR = 500;
    public static final int CODE_INTERNAL_SERVER_ERROR = 500;

    // Default Messages
    public static final String MSG_SUCCESS = "success";
    public static final String MSG_CREATED = "created";
    public static final String MSG_ACCEPTED = "accepted";
    public static final String MSG_NO_CONTENT = "no content";
    public static final String MSG_BAD_REQUEST = "Bad Request";
    public static final String MSG_UNAUTHORIZED = "Unauthorized";
    public static final String MSG_FORBIDDEN = "Forbidden";
    public static final String MSG_NOT_FOUND = "Resource Not Found";
    public static final String MSG_METHOD_NOT_ALLOWED = "Method Not Allowed";
    public static final String MSG_NOT_ACCEPTABLE = "Media Type Not Acceptable";
    public static final String MSG_INTERNAL_SERVER_ERROR = "Internal Server Error";

    /**
     * Success response with data.
     */
    public static <T> Result<T> ok(T data) {
        return ok(data, null);
    }

    /**
     * Success response with data and trace ID.
     */
    public static <T> Result<T> ok(T data, String traceId) {
        return new Result<>(
            CODE_OK,
            MSG_SUCCESS,
            data,
            java.time.Instant.now().toString(),
            traceId,
            true
        );
    }

    /**
     * Success response without data.
     */
    public static <T> Result<T> ok() {
        return ok(null);
    }

    /**
     * Error response.
     */
    public static <T> Result<T> fail(Integer code, String message) {
        return fail(code, message, null, null);
    }

    /**
     * Error response with data.
     */
    public static <T> Result<T> fail(Integer code, String message, T data) {
        return fail(code, message, data, null);
    }

    /**
     * Error response with data and trace ID.
     */
    public static <T> Result<T> fail(Integer code, String message, T data, String traceId) {
        return new Result<>(
            code,
            message,
            data,
            java.time.Instant.now().toString(),
            traceId,
            false
        );
    }

    /**
     * Error response with trace ID (no data).
     */
    public static <T> Result<T> failWithTraceId(Integer code, String message, String traceId) {
        return fail(code, message, null, traceId);
    }

    /**
     * Error response from BusinessException.
     */
    public static <T> Result<T> fail(com.nexora.common.exception.BusinessException ex) {
        return fail(ex.getCode(), ex.getMessage(), null, ex.getTraceId());
    }

    /**
     * Error response from BusinessException with data.
     */
    public static <T> Result<T> fail(com.nexora.common.exception.BusinessException ex, T data) {
        return fail(ex.getCode(), ex.getMessage(), data, ex.getTraceId());
    }

    /**
     * Accepted response (202).
     */
    public static <T> Result<T> accepted(T data) {
        return accepted(data, null);
    }

    /**
     * Accepted response with trace ID.
     */
    public static <T> Result<T> accepted(T data, String traceId) {
        return new Result<>(
            CODE_ACCEPTED,
            MSG_ACCEPTED,
            data,
            java.time.Instant.now().toString(),
            traceId,
            true
        );
    }

    /**
     * Created response (201).
     */
    public static <T> Result<T> created(T data) {
        return created(data, null);
    }

    /**
     * Created response with trace ID.
     */
    public static <T> Result<T> created(T data, String traceId) {
        return new Result<>(
            CODE_CREATED,
            MSG_CREATED,
            data,
            java.time.Instant.now().toString(),
            traceId,
            true
        );
    }

    /**
     * No content response (204).
     */
    public static <T> Result<T> noContent() {
        return noContent(null);
    }

    /**
     * No content response with trace ID.
     */
    public static <T> Result<T> noContent(String traceId) {
        return new Result<>(
            CODE_NO_CONTENT,
            MSG_NO_CONTENT,
            null,
            java.time.Instant.now().toString(),
            traceId,
            true
        );
    }

    /**
     * Generate a new trace ID.
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString();
    }
}
