package com.nexora.audit.repository;

import com.nexora.audit.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link AuditLog} entities.
 *
 * @author sujie
 * @since 1.0.0
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Find audit logs by user ID.
     */
    List<AuditLog> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find audit logs by action type.
     */
    List<AuditLog> findByActionOrderByCreatedAtDesc(String action);

    /**
     * Find audit logs by success status.
     */
    List<AuditLog> findBySuccessOrderByCreatedAtDesc(boolean success);

    /**
     * Find audit logs by user ID and action.
     */
    List<AuditLog> findByUserIdAndActionOrderByCreatedAtDesc(Long userId, String action);

    /**
     * Find audit logs within a date range.
     */
    List<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(Instant start, Instant end);

    /**
     * Find audit logs by correlation ID.
     */
    List<AuditLog> findByCorrelationIdOrderByCreatedAtDesc(String correlationId);

    /**
     * Find audit logs by module name.
     */
    List<AuditLog> findByModuleNameOrderByCreatedAtDesc(String moduleName);

    /**
     * Count failed audit logs by user ID.
     */
    long countByUserIdAndSuccessFalseAndCreatedAtAfter(Long userId, Instant after);

    /**
     * Count failed audit logs by action.
     */
    long countByActionAndSuccessFalse(String action);
}
