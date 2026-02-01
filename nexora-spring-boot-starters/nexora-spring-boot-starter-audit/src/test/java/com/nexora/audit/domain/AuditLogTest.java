package com.nexora.audit.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link AuditLog}.
 */
class AuditLogTest {

    @Test
    void testSuccessAuditLog() {
        AuditLog auditLog = AuditLog.success("LOGIN", 1L);

        assertEquals("LOGIN", auditLog.getAction());
        assertEquals(1L, auditLog.getUserId());
        assertTrue(auditLog.isSuccess());
        assertFalse(auditLog.isFailure());
        assertNull(auditLog.getErrorMessage());
    }

    @Test
    void testFailureAuditLog() {
        AuditLog auditLog = AuditLog.failure("LOGIN", 1L, "Invalid credentials");

        assertEquals("LOGIN", auditLog.getAction());
        assertEquals(1L, auditLog.getUserId());
        assertFalse(auditLog.isSuccess());
        assertTrue(auditLog.isFailure());
        assertEquals("Invalid credentials", auditLog.getErrorMessage());
    }

    @Test
    void testMarkFailed() {
        AuditLog auditLog = AuditLog.success("LOGIN", 1L);
        assertTrue(auditLog.isSuccess());

        auditLog.markFailed("Connection timeout");
        assertFalse(auditLog.isSuccess());
        assertTrue(auditLog.isFailure());
        assertEquals("Connection timeout", auditLog.getErrorMessage());
    }

    @Test
    void testMarkSuccess() {
        AuditLog auditLog = AuditLog.failure("LOGIN", 1L, "Error");
        assertFalse(auditLog.isSuccess());

        auditLog.markSuccess();
        assertTrue(auditLog.isSuccess());
        assertNull(auditLog.getErrorMessage());
    }

    @Test
    void testSetters() {
        AuditLog auditLog = AuditLog.success("LOGIN", 1L);

        auditLog.setIpAddress("192.168.1.1");
        assertEquals("192.168.1.1", auditLog.getIpAddress());

        auditLog.setUserAgent("Mozilla/5.0");
        assertEquals("Mozilla/5.0", auditLog.getUserAgent());

        auditLog.setRequestUri("/api/users/1");
        assertEquals("/api/users/1", auditLog.getRequestUri());

        auditLog.setHttpMethod("GET");
        assertEquals("GET", auditLog.getHttpMethod());

        auditLog.setModuleName("authsrv");
        assertEquals("authsrv", auditLog.getModuleName());

        auditLog.setEnvironment("prod");
        assertEquals("prod", auditLog.getEnvironment());

        auditLog.setClientId("web-app");
        assertEquals("web-app", auditLog.getClientId());

        auditLog.setSessionId("session-123");
        assertEquals("session-123", auditLog.getSessionId());

        auditLog.setCorrelationId("corr-456");
        assertEquals("corr-456", auditLog.getCorrelationId());
    }

    @Test
    void testActions() {
        assertNotNull(AuditLog.Actions.LOGIN);
        assertNotNull(AuditLog.Actions.LOGOUT);
        assertNotNull(AuditLog.Actions.USER_CREATE);
        assertNotNull(AuditLog.Actions.USER_DELETE);
        assertNotNull(AuditLog.Actions.SYSTEM_ERROR);
    }

    @Test
    void testInvalidAction() {
        assertThrows(IllegalArgumentException.class, () -> AuditLog.success("", 1L));
        assertThrows(IllegalArgumentException.class, () -> AuditLog.success(null, 1L));
        assertThrows(IllegalArgumentException.class, () -> AuditLog.success("A".repeat(65), 1L));
    }
}
