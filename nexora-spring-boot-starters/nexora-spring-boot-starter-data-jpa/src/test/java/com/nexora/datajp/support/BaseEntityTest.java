package com.nexora.datajp.support;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link BaseEntity}.
 */
class BaseEntityTest {

    @Test
    void testBaseEntity() {
        TestEntity entity = new TestEntity();

        assertNotNull(entity);
        assertNull(entity.getId());
        assertTrue(entity.isNew());

        entity.setId(1L);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        entity.setCreatedBy("system");
        entity.setUpdatedBy("system");

        assertEquals(1L, entity.getId());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
        assertEquals("system", entity.getCreatedBy());
        assertEquals("system", entity.getUpdatedBy());
        assertFalse(entity.isNew());
    }

    @Test
    void testEqualsAndHashCode() {
        TestEntity entity1 = new TestEntity();
        TestEntity entity2 = new TestEntity();

        // Both are new (null ID), should NOT be equal
        assertNotEquals(entity1, entity2);

        entity1.setId(1L);
        entity2.setId(1L);

        // Same ID, should be equal
        assertEquals(entity1, entity2);

        entity2.setId(2L);

        // Different IDs, should NOT be equal
        assertNotEquals(entity1, entity2);
    }

    static class TestEntity extends BaseEntity {
    }
}
