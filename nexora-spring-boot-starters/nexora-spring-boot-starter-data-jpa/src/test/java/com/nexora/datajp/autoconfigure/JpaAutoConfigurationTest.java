package com.nexora.datajp.autoconfigure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link JpaAutoConfiguration}.
 */
class JpaAutoConfigurationTest {

    @Test
    void testPropertiesCreation() {
        JpaProperties properties = new JpaProperties();

        assertNotNull(properties);
        assertTrue(properties.getAuditing().isEnabled());
        assertTrue(properties.getLazyLoading().isEnabled());
    }

    @Test
    void testPropertiesDefaults() {
        JpaProperties properties = new JpaProperties();

        assertTrue(properties.getAuditing().isEnabled());
        assertTrue(properties.getLazyLoading().isEnabled());
    }
}
