package com.nexora.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link BusinessException}.
 */
@DisplayName("BusinessException Tests")
class BusinessExceptionTest {

    @Test
    @DisplayName("Default business exception should have code 400")
    void testDefaultBusinessException() {
        BusinessException exception = new BusinessException("Operation failed");

        assertAll("Default business exception",
            () -> assertEquals("Operation failed", exception.getMessage()),
            () -> assertEquals(400, exception.getCode()),
            () -> assertNull(exception.getCause())
        );
    }

    @Test
    @DisplayName("Business exception with custom code should preserve code")
    void testBusinessExceptionWithCustomCode() {
        BusinessException exception = new BusinessException(404, "Resource not found");

        assertAll("Custom code business exception",
            () -> assertEquals("Resource not found", exception.getMessage()),
            () -> assertEquals(404, exception.getCode())
        );
    }

    @Test
    @DisplayName("Business exception with cause should wrap original exception")
    void testBusinessExceptionWithCause() {
        Throwable cause = new IllegalArgumentException("Invalid parameter");
        BusinessException exception = new BusinessException(400, "Invalid input", cause);

        assertAll("Business exception with cause",
            () -> assertEquals("Invalid input", exception.getMessage()),
            () -> assertEquals(400, exception.getCode()),
            () -> assertEquals(cause, exception.getCause())
        );
    }

    @Test
    @DisplayName("Business exception with cause should preserve message")
    void testBusinessExceptionCauseMessage() {
        Throwable cause = new RuntimeException("Database connection failed");
        BusinessException exception = new BusinessException(503, "Service unavailable", cause);

        assertEquals("Service unavailable", exception.getMessage());
        assertEquals(503, exception.getCode());
    }

    @Test
    @DisplayName("Business exception should be runtime exception")
    void testIsRuntimeException() {
        BusinessException exception = new BusinessException("Error");

        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("Business exception with error code enum")
    void testBusinessExceptionWithErrorCode() {
        BusinessException exception = new BusinessException(BusinessException.ErrorCode.USER_NOT_FOUND);

        assertAll("Exception with error code enum",
            () -> assertEquals(BusinessException.ErrorCode.USER_NOT_FOUND.getCode(), exception.getCode()),
            () -> assertEquals(BusinessException.ErrorCode.USER_NOT_FOUND.getDefaultMessage(), exception.getMessage()),
            () -> assertEquals(BusinessException.ErrorCode.USER_NOT_FOUND, exception.getErrorCode())
        );
    }

    @Test
    @DisplayName("Business exception with context")
    void testBusinessExceptionWithContext() {
        BusinessException exception = new BusinessException(BusinessException.ErrorCode.USER_NOT_FOUND)
            .withContext("userId", "123")
            .withContext("email", "test@example.com");

        assertAll("Exception with context",
            () -> assertEquals("123", exception.getContextValue("userId")),
            () -> assertEquals("test@example.com", exception.getContextValue("email")),
            () -> assertEquals(2, exception.getContext().size())
        );
    }

    @Test
    @DisplayName("Business exception should have trace ID")
    void testBusinessExceptionHasTraceId() {
        BusinessException exception = new BusinessException("Test error");

        assertNotNull(exception.getTraceId());
        assertFalse(exception.getTraceId().isEmpty());
    }

    @Test
    @DisplayName("Static factory method should create exception")
    void testStaticFactoryMethod() {
        BusinessException exception = BusinessException.of(BusinessException.ErrorCode.USER_DISABLED);

        assertEquals(BusinessException.ErrorCode.USER_DISABLED.getCode(), exception.getCode());
    }
}
