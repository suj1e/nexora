package com.nexora.audit.service;

import com.nexora.audit.domain.AuditLog;
import com.nexora.audit.repository.AuditLogRepository;
import com.nexora.datajp.support.Entities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Audit log service for recording system events.
 *
 * <p>Provides both synchronous and asynchronous logging methods.
 * Async methods return CompletableFuture for non-blocking operations.
 *
 * <p>Usage:
 * <pre>
 * &#64;Autowired
 * private AuditLogService auditLogService;
 *
 * // Synchronous logging
 * auditLogService.log("USER_CREATE", userId, request);
 * auditLogService.logFailure("USER_DELETE", userId, "Not authorized", request);
 *
 * // Asynchronous logging (non-blocking)
 * auditLogService.logAsync("LOGIN", userId, request);
 * </pre>
 *
 * @author sujie
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Log a successful audit event.
     *
     * @param action the action performed
     * @param userId the user who performed the action
     * @return the created audit log
     */
    @Transactional
    public AuditLog log(String action, Long userId) {
        return log(action, userId, null, null);
    }

    /**
     * Log a failed audit event.
     *
     * @param action the action that failed
     * @param userId the user who attempted the action
     * @param errorMessage error description
     * @return the created audit log
     */
    @Transactional
    public AuditLog logFailure(String action, Long userId, String errorMessage) {
        AuditLog auditLog = AuditLog.failure(action, userId, errorMessage);
        return auditLogRepository.save(auditLog);
    }

    /**
     * Log a successful audit event with request context.
     *
     * @param action the action performed
     * @param userId the user who performed the action
     * @param request the HTTP request
     * @return the created audit log
     */
    @Transactional
    public AuditLog log(String action, Long userId, HttpServletRequest request) {
        AuditLog auditLog = populateFromRequest(action, userId, request);
        return auditLogRepository.save(auditLog);
    }

    /**
     * Log a failed audit event with request context.
     *
     * @param action the action that failed
     * @param userId the user who attempted the action
     * @param errorMessage error description
     * @param request the HTTP request
     * @return the created audit log
     */
    @Transactional
    public AuditLog logFailure(String action, Long userId, String errorMessage, HttpServletRequest request) {
        AuditLog auditLog = populateFromRequest(action, userId, request);
        auditLog.markFailed(errorMessage);
        return auditLogRepository.save(auditLog);
    }

    /**
     * Log a successful audit event with context data.
     *
     * @param action the action performed
     * @param userId the user who performed the action
     * @param contextData additional context as JSON
     * @param request the HTTP request (optional)
     * @return the created audit log
     */
    @Transactional
    public AuditLog log(String action, Long userId, Map<String, Object> contextData, HttpServletRequest request) {
        AuditLog auditLog = populateFromRequest(action, userId, request);
        if (contextData != null && !contextData.isEmpty()) {
            auditLog.setContextData(serializeContextData(contextData));
        }
        return auditLogRepository.save(auditLog);
    }

    /**
     * Asynchronously log a successful audit event.
     *
     * @param action the action performed
     * @param userId the user who performed the action
     * @return CompletableFuture with the created audit log
     */
    @Async
    public CompletableFuture<AuditLog> logAsync(String action, Long userId) {
        return CompletableFuture.supplyAsync(() -> log(action, userId));
    }

    /**
     * Asynchronously log a failed audit event.
     *
     * @param action the action that failed
     * @param userId the user who attempted the action
     * @param errorMessage error description
     * @return CompletableFuture with the created audit log
     */
    @Async
    public CompletableFuture<AuditLog> logFailureAsync(String action, Long userId, String errorMessage) {
        return CompletableFuture.supplyAsync(() -> logFailure(action, userId, errorMessage));
    }

    /**
     * Asynchronously log with request context.
     *
     * @param action the action performed
     * @param userId the user who performed the action
     * @param request the HTTP request
     * @return CompletableFuture with the created audit log
     */
    @Async
    public CompletableFuture<AuditLog> logAsync(String action, Long userId, HttpServletRequest request) {
        return CompletableFuture.supplyAsync(() -> log(action, userId, request));
    }

    /**
     * Find audit logs by user ID.
     *
     * @param userId the user ID
     * @return list of audit logs
     */
    public java.util.List<AuditLog> findByUserId(Long userId) {
        return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Find audit logs by action type.
     *
     * @param action the action type
     * @return list of audit logs
     */
    public java.util.List<AuditLog> findByAction(String action) {
        return auditLogRepository.findByActionOrderByCreatedAtDesc(action);
    }

    /**
     * Find audit logs within a date range.
     *
     * @param start the start of the range
     * @param end the end of the range
     * @return list of audit logs
     */
    public java.util.List<AuditLog> findByDateRange(Instant start, Instant end) {
        return auditLogRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(start, end);
    }

    /**
     * Find audit logs by correlation ID.
     *
     * @param correlationId the correlation ID
     * @return list of audit logs
     */
    public java.util.List<AuditLog> findByCorrelationId(String correlationId) {
        return auditLogRepository.findByCorrelationIdOrderByCreatedAtDesc(correlationId);
    }

    /**
     * Count failed login attempts for a user after a given time.
     *
     * @param userId the user ID
     * @param after the time threshold
     * @return count of failed attempts
     */
    public long countFailedLoginAttempts(Long userId, Instant after) {
        return auditLogRepository.countByUserIdAndSuccessFalseAndCreatedAtAfter(userId, after);
    }

    /**
     * Count failed attempts for a specific action.
     *
     * @param action the action type
     * @return count of failed attempts
     */
    public long countFailedAttempts(String action) {
        return auditLogRepository.countByActionAndSuccessFalse(action);
    }

    /**
     * Find audit log by ID.
     *
     * @param id the audit log ID
     * @return the audit log if found
     */
    public Optional<AuditLog> findById(Long id) {
        return auditLogRepository.findById(id);
    }

    /**
     * Populate audit log from HTTP request.
     */
    private AuditLog populateFromRequest(String action, Long userId, HttpServletRequest request) {
        AuditLog auditLog = AuditLog.success(action, userId);

        if (request != null) {
            auditLog.setIpAddress(getClientIpAddress(request));
            auditLog.setUserAgent(request.getHeader("User-Agent"));
            auditLog.setRequestUri(request.getRequestURI());
            auditLog.setHttpMethod(request.getMethod());
            auditLog.setSessionId(request.getSession(false) != null ? request.getSession(false).getId() : null);
            auditLog.setCorrelationId(request.getHeader("X-Correlation-ID"));
            auditLog.setClientId(request.getHeader("X-Client-ID"));
        }

        return auditLog;
    }

    /**
     * Extract client IP address from request.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Handle multiple IPs in X-Forwarded-For (take the first one)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * Serialize context data to JSON.
     */
    private String serializeContextData(Map<String, Object> contextData) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return objectMapper.writeValueAsString(contextData);
        } catch (Exception e) {
            log.warn("Failed to serialize context data", e);
            return null;
        }
    }
}
