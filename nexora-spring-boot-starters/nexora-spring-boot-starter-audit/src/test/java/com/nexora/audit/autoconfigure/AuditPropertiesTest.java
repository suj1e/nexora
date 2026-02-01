package com.nexora.audit.autoconfigure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link AuditProperties}.
 */
class AuditPropertiesTest {

    @Test
    void testDefaultValues() {
        AuditProperties properties = new AuditProperties();

        assertTrue(properties.isEnabled());
        assertTrue(properties.isAsync());
        assertEquals(90, properties.getRetentionDays());
        assertFalse(properties.isIncludeBodies());

        assertNotNull(properties.getSensitiveData());
        assertFalse(properties.getSensitiveData().isMaskIp());
        assertTrue(properties.getSensitiveData().isMaskUserAgent());
        assertTrue(properties.getSensitiveData().isMaskSessionId());
    }

    @Test
    void testSensitiveDataDefaults() {
        AuditProperties.SensitiveData sensitiveData = new AuditProperties.SensitiveData();

        assertFalse(sensitiveData.isMaskIp());
        assertTrue(sensitiveData.isMaskUserAgent());
        assertTrue(sensitiveData.isMaskSessionId());
    }
}
