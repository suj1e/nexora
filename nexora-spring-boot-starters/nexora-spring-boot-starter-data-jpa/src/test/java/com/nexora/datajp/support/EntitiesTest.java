package com.nexora.datajp.support;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link Entities}.
 */
class EntitiesTest {

    @Test
    void testCreateWithMapper() {
        @SuppressWarnings("unchecked")
        JpaRepository<TestEntity, Long> repo = mock(JpaRepository.class);
        TestEntity saved = new TestEntity("input");
        saved.setId(1L);
        when(repo.save(any(TestEntity.class))).thenReturn(saved);

        Optional<TestEntity> result = Entities.create(repo)
            .with("input", TestEntity::of)
            .execute();

        assertTrue(result.isPresent());
        assertEquals("input", result.get().getName());
    }

    @Test
    void testCreateWithSupplier() {
        @SuppressWarnings("unchecked")
        JpaRepository<TestEntity, Long> repo = mock(JpaRepository.class);
        TestEntity saved = new TestEntity("created");
        saved.setId(1L);
        when(repo.save(any(TestEntity.class))).thenReturn(saved);

        Optional<TestEntity> result = Entities.create(repo)
            .supply(() -> new TestEntity("created"))
            .execute();

        assertTrue(result.isPresent());
        assertEquals("created", result.get().getName());
    }

    @Test
    void testUpdateWithMapper() {
        @SuppressWarnings("unchecked")
        JpaRepository<TestEntity, Long> repo = mock(JpaRepository.class);
        TestEntity existing = new TestEntity("original");
        existing.setId(1L);
        TestEntity updated = new TestEntity("original-update");
        updated.setId(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(any(TestEntity.class))).thenReturn(updated);

        Optional<TestEntity> result = Entities.update(repo, 1L)
            .with("update", TestEntity::apply)
            .execute();

        assertTrue(result.isPresent());
        assertEquals("original-update", result.get().getName());
    }

    @Test
    void testUpdateWithFunction() {
        @SuppressWarnings("unchecked")
        JpaRepository<TestEntity, Long> repo = mock(JpaRepository.class);
        TestEntity existing = new TestEntity("original");
        existing.setId(1L);
        TestEntity updated = new TestEntity("modified");
        updated.setId(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(any(TestEntity.class))).thenReturn(updated);

        Optional<TestEntity> result = Entities.update(repo, 1L)
            .apply(entity -> {
                entity.setName("modified");
                return entity;
            })
            .execute();

        assertTrue(result.isPresent());
        assertEquals("modified", result.get().getName());
    }

    @Test
    void testDelete() {
        @SuppressWarnings("unchecked")
        JpaRepository<TestEntity, Long> repo = mock(JpaRepository.class);
        TestEntity entity = new TestEntity("to-delete");
        entity.setId(1L);

        when(repo.existsById(1L)).thenReturn(true);
        when(repo.findById(1L)).thenReturn(Optional.of(entity));

        assertTrue(Entities.delete(repo, 1L));

        // Second call should return false as entity is "deleted"
        when(repo.existsById(1L)).thenReturn(false);
        when(repo.findById(1L)).thenReturn(Optional.empty());
        assertFalse(Entities.delete(repo, 1L));
    }

    // Test entity class

    static class TestEntity {
        private Long id;
        private String name;

        public TestEntity() {
        }

        public TestEntity(String name) {
            this.name = name;
        }

        public static TestEntity of(String input) {
            return new TestEntity(input);
        }

        public TestEntity apply(String update) {
            this.name = this.name + "-" + update;
            return this;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
