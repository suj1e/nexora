package com.nexora.datajp.support;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Base JPA entity with audit fields.
 *
 * <p>Provides standard auditing capabilities:
 * <ul>
 *   <li>id - Auto-generated primary key</li>
 *   <li>createdAt - Timestamp when entity was first persisted</li>
 *   <li>updatedAt - Timestamp of last modification</li>
 *   <li>createdBy - User/system that created the entity</li>
 *   <li>updatedBy - User/system that last modified the entity</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>
 * &#64;Entity
 * public class User extends BaseEntity {
 *     // ... entity fields
 * }
 * </pre>
 *
 * @author sujie
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @CreatedBy
    @Column(name = "created_by", length = 100)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    /**
     * Get entity ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Set entity ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get creation timestamp.
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Set creation timestamp.
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Get last modification timestamp.
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Set last modification timestamp.
     */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Get creator user/system.
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Set creator user/system.
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get last modifier user/system.
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set last modifier user/system.
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Check if entity is not yet persisted (has no ID).
     */
    public boolean isNew() {
        return id == null;
    }

    /**
     * Equality based on ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        BaseEntity that = (BaseEntity) o;
        return id != null && id.equals(that.id);
    }

    /**
     * Hash code based on ID.
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
